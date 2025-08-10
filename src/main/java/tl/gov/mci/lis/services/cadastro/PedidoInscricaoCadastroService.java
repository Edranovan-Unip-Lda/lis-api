package tl.gov.mci.lis.services.cadastro;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.mappers.FaturaMapper;
import tl.gov.mci.lis.dtos.mappers.PedidoInscricaoCadastroMapper;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.models.pagamento.Taxa;
import tl.gov.mci.lis.repositories.cadastro.PedidoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.endereco.EnderecoRepository;
import tl.gov.mci.lis.repositories.pagamento.FaturaRepository;
import tl.gov.mci.lis.repositories.pagamento.TaxaRepository;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoInscricaoCadastroService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoInscricaoCadastroService.class);
    private final PedidoInscricaoCadastroRepository pedidoInscricaoCadastroRepository;
    private final EnderecoRepository enderecoRepository;
    private final PedidoInscricaoCadastroMapper pedidoInscricaoCadastroMapper;
    private final FaturaRepository faturaRepository;
    private final FaturaMapper faturaMapper;
    private final TaxaRepository taxaRepository;
    private final EntityManager entityManager;

    public PedidoInscricaoCadastroDto getByAplicanteId(Long aplicanteId) {
        logger.info("Getting Inscrição for Aplicante id: {}", aplicanteId);

        return pedidoInscricaoCadastroRepository
                .findByAplicante_Id(aplicanteId)
                .map(obj -> {
                    obj.setEmpresaSede(enderecoRepository.getFromId(obj.getEmpresaSede().getId()));
                    obj.setLocalEstabelecimento(enderecoRepository.getFromId(obj.getLocalEstabelecimento().getId()));
                    return pedidoInscricaoCadastroMapper.toDto(obj);
                })
                .orElse(null);  // ← return null when no record exists
    }

    @Transactional
    public FaturaDto createFatura(Long pedidoInscricaoCadastroId, Fatura obj) {
        logger.info("Criando fatura: {}", obj);
        obj.setPedidoInscricaoCadastro(pedidoInscricaoCadastroRepository.getReferenceById(pedidoInscricaoCadastroId));
        obj.setStatus(FaturaStatus.EMITIDA);

        // Fetch managed Taxa entities from DB
        Set<Taxa> managedTaxas = obj.getTaxas().stream()
                .map(taxa -> taxaRepository.getReferenceById(taxa.getId()))
                .collect(Collectors.toSet());

        obj.setTaxas(managedTaxas);

        entityManager.persist(obj);

        return faturaMapper.toDto(obj);
    }

    @Transactional
    public FaturaDto updateFatura(Long pedidoId, Long faturaId, Fatura incoming) {
        Fatura entity = faturaRepository
                .findByIdAndPedidoInscricaoCadastro_Id(faturaId, pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Fatura não encontrada"));

        // simples
        entity.setNomeEmpresa(incoming.getNomeEmpresa());
        entity.setSociedadeComercial(incoming.getSociedadeComercial());
        entity.setSuperficie(incoming.getSuperficie());
        entity.setTotal(incoming.getTotal());

        // === TAXAS ===
        if (incoming.getTaxas() != null) {
            // Ensure equals/hashCode(Taxa) is ID-based!
            Set<Long> targetIds = incoming.getTaxas().stream()
                    .map(EntityDB::getId).filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Load managed collection (causes the SELECT you see)
            Set<Taxa> current = entity.getTaxas();

            // Remove
            for (Iterator<Taxa> it = current.iterator(); it.hasNext(); ) {
                Taxa t = it.next();
                if (!targetIds.contains(t.getId())) {
                    it.remove();                 // marks collection dirty
                    t.getFaturas().remove(entity); // keep inverse in sync
                }
            }
            // Add
            Set<Long> currentIds = current.stream().map(EntityDB::getId).collect(Collectors.toSet());
            for (Long id : targetIds) {
                if (!currentIds.contains(id)) {
                    Taxa ref = taxaRepository.getReferenceById(id); // proxy, no SELECT
                    current.add(ref);               // owning side updated
                    ref.getFaturas().add(entity);   // inverse side sync (optional but good)
                }
            }
        }

        return faturaMapper.toDto(entity);
    }
}
