package tl.gov.mci.lis.services.cadastro;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.mappers.FaturaMapper;
import tl.gov.mci.lis.dtos.mappers.PedidoInscricaoCadastroMapper;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.repositories.cadastro.PedidoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.dadosmestre.AtividadeEconomicaRepository;
import tl.gov.mci.lis.repositories.endereco.EnderecoRepository;
import tl.gov.mci.lis.repositories.pagamento.FaturaRepository;
import tl.gov.mci.lis.repositories.pagamento.TaxaRepository;

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
    private final AtividadeEconomicaRepository atividadeEconomicaRepository;

    public PedidoInscricaoCadastro getById(Long id) {
        logger.info("Obtendo pedido de inscricao pelo id: {}", id);
        PedidoInscricaoCadastro obj = pedidoInscricaoCadastroRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Pedido de inscricao nao encontrado"));
        obj.setSede(enderecoRepository.getFromId(obj.getSede().getId()));
        return obj;
    }

    public PedidoInscricaoCadastroDto getByAplicanteId(Long aplicanteId) {
        logger.info("Getting Inscrição for Aplicante id: {}", aplicanteId);

        return pedidoInscricaoCadastroRepository
                .findByAplicante_Id(aplicanteId)
                .map(obj -> {
                    obj.setSede(enderecoRepository.getFromId(obj.getSede().getId()));
                    return pedidoInscricaoCadastroMapper.toDto(obj);
                })
                .orElse(null);  // ← return null when no record exists
    }

    public FaturaDto createFatura(Long pedidoInscricaoCadastroId, Fatura obj) {
        logger.info("Criando fatura: {}", obj);
        obj.setPedidoInscricaoCadastro(pedidoInscricaoCadastroRepository.getReferenceById(pedidoInscricaoCadastroId));
        obj.setTaxa(taxaRepository.getReferenceById(obj.getTaxa().getId()));
        obj.setAtividadeDeclarada(atividadeEconomicaRepository.getReferenceById(obj.getAtividadeDeclarada().getId()));
        obj.setStatus(FaturaStatus.EMITIDA);
        return faturaMapper.toDto(faturaRepository.save(obj));
    }

    public FaturaDto updateFatura(Long pedidoInscricaoCadastroId, Long faturaId, Fatura obj) {
        logger.info("Atualizando fatura: {}", obj);
        return faturaRepository.findByIdAndPedidoInscricaoCadastro_Id(faturaId, pedidoInscricaoCadastroId)
                .map(fatura -> {
                    fatura.setTaxa(taxaRepository.getReferenceById(obj.getTaxa().getId()));
                    fatura.setAtividadeDeclarada(atividadeEconomicaRepository.getReferenceById(obj.getAtividadeDeclarada().getId()));
                    fatura.setNomeEmpresa(obj.getNomeEmpresa());
                    fatura.setSociedadeComercial(obj.getSociedadeComercial());
                    return faturaMapper.toDto(faturaRepository.save(fatura));
                })
                .orElse(null);
    }
}
