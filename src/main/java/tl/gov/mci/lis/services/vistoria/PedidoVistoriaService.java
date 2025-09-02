package tl.gov.mci.lis.services.vistoria;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.mappers.FaturaMapper;
import tl.gov.mci.lis.dtos.mappers.VistoriaMapper;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaDto;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.models.pagamento.Taxa;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;
import tl.gov.mci.lis.repositories.atividade.PedidoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.ClasseAtividadeRepository;
import tl.gov.mci.lis.repositories.pagamento.FaturaRepository;
import tl.gov.mci.lis.repositories.pagamento.TaxaRepository;
import tl.gov.mci.lis.repositories.vistoria.PedidoVistoriaRepository;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoVistoriaService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoVistoriaService.class);
    private final PedidoVistoriaRepository pedidoVistoriaRepository;
    private final ClasseAtividadeRepository classeAtividadeRepository;
    private final EntityManager entityManager;
    private final VistoriaMapper vistoriaMapper;
    private final TaxaRepository taxaRepository;
    private final FaturaMapper faturaMapper;
    private final FaturaRepository faturaRepository;
    private final PedidoLicencaAtividadeRepository pedidoLicencaAtividadeRepository;

    @Transactional
    public PedidoVistoria create(Long pedidoLicencaAtividadeId, PedidoVistoria obj) {
        logger.info("Criando pedido de vistoria com aplicanteId: {}", pedidoLicencaAtividadeId);
        obj.setPedidoLicencaAtividade(pedidoLicencaAtividadeRepository.getReferenceById(pedidoLicencaAtividadeId));
        obj.setClasseAtividade(classeAtividadeRepository.getReferenceById(obj.getClasseAtividade().getId()));
        obj.setStatus(PedidoStatus.SUBMETIDO);
        entityManager.persist(obj);
        return obj;
    }

    @Transactional
    public PedidoVistoria update(Long pedidoId, Long pedidoLicencaAtividadeId, PedidoVistoria obj) {
        logger.info("Atualizando pedido de vistoria com id: {}", pedidoId);
        PedidoVistoria entity = pedidoVistoriaRepository.findByIdAndPedidoLicencaAtividade_Id(pedidoId, pedidoLicencaAtividadeId)
                .orElseThrow(() -> {
                    logger.error("PedidoVistoria nao encontrado");
                    return new ResourceNotFoundException("PedidoVistoria nao encontrado");
                });
        upsertEndereco(entity::getEmpresaSede, entity::setEmpresaSede, obj.getEmpresaSede());
        upsertEndereco(entity::getLocalEstabelecimento, entity::setLocalEstabelecimento, obj.getLocalEstabelecimento());

        if (entity.getClasseAtividade() != null) {
            entity.setClasseAtividade(classeAtividadeRepository.getReferenceById(obj.getClasseAtividade().getId()));
        }

        setIfChanged(entity::setTipoVistoria, entity.getTipoVistoria(), obj.getTipoVistoria());
        setIfChanged(entity::setNomeEmpresa, entity.getNomeEmpresa(), obj.getNomeEmpresa());
        setIfChanged(entity::setEmpresaNumeroRegistoComercial, entity.getEmpresaNumeroRegistoComercial(), obj.getEmpresaNumeroRegistoComercial());
        setIfChanged(entity::setEmpresaNif, entity.getEmpresaNif(), obj.getEmpresaNif());
        setIfChanged(entity::setEmpresaTelefone, entity.getEmpresaTelefone(), obj.getEmpresaTelefone());
        setIfChanged(entity::setEmpresaEmail, entity.getEmpresaEmail(), obj.getEmpresaEmail());
        setIfChanged(entity::setEmpresaTelemovel, entity.getEmpresaTelemovel(), obj.getEmpresaTelemovel());
        setIfChanged(entity::setEmpresaGerente, entity.getEmpresaGerente(), obj.getEmpresaGerente());
        setIfChanged(entity::setNomeEstabelecimento, entity.getNomeEstabelecimento(), obj.getNomeEstabelecimento());
        setIfChanged(entity::setTipoEmpresa, entity.getTipoEmpresa(), obj.getTipoEmpresa());
        setIfChanged(entity::setTipoEstabelecimento, entity.getTipoEstabelecimento(), obj.getTipoEstabelecimento());
        setIfChanged(entity::setRisco, entity.getRisco(), obj.getRisco());
        setIfChanged(entity::setAtividade, entity.getAtividade(), obj.getAtividade());
        setIfChanged(entity::setTipoAtividade, entity.getTipoAtividade(), obj.getTipoAtividade());
        setIfChanged(entity::setAlteracoes, entity.getAlteracoes(), obj.getAlteracoes());
        setIfChanged(entity::setObservacao, entity.getObservacao(), obj.getObservacao());

        return entity;
    }

    @Transactional(readOnly = true)
    public Set<PedidoVistoriaDto> getBypedidoLicencaAtividadeId(Long pedidoLicencaAtividadeId) {
        logger.info("Obtendo Pedido Vistoria pelo PedidoLicencaAtividade Id: {}", pedidoLicencaAtividadeId);

        return pedidoVistoriaRepository.findByPedidoLicencaAtividade_Id(pedidoLicencaAtividadeId)
                .stream()
                .map(vistoriaMapper::toDto).collect(Collectors.toSet());
    }

    @Transactional
    public FaturaDto createFatura(Long pedidoId, Fatura obj) {
        logger.info("Criando fatura: {}", obj);
        PedidoVistoria pedido = pedidoVistoriaRepository.findDetailById(pedidoId)
                .orElseThrow(() -> {
                    logger.error("PedidoLicencaAtividade nao encontrado");
                    return new ResourceNotFoundException("PedidoLicencaAtividade nao encontrado");
                });
        obj.setStatus(FaturaStatus.EMITIDA);

        // Fetch managed Taxa entities from DB
        Set<Taxa> managedTaxas = obj.getTaxas().stream()
                .map(taxa -> taxaRepository.getReferenceById(taxa.getId()))
                .collect(Collectors.toSet());

        obj.setTaxas(managedTaxas);

        pedido.setFatura(obj);

        entityManager.persist(obj);
        return faturaMapper.toDto(obj);
    }

    @Transactional
    public FaturaDto updateFatura(Long pedidoId, Long faturaId, Fatura incoming) {
        Fatura entity = faturaRepository
                .findByIdAndPedidoVistoria_Id(faturaId, pedidoId)
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

    /**
     * Inserts, updates, or removes an Endereco entity linked to another entity.
     * <p>
     * Depending on the state of the input `incoming` object, this method performs one of the following actions:
     * 1. If `incoming` is null, it removes the existing `Endereco`.
     * 2. If `incoming` has a valid ID, it updates the existing `Endereco` with changes from `incoming`.
     * 3. If `incoming` does not have an ID, it creates a new `Endereco` and links it.
     *
     * @param getter   A Supplier that retrieves the current Endereco associated with the entity.
     * @param setter   A Consumer that accepts and sets a new or updated Endereco to the entity.
     * @param incoming The incoming Endereco object to be inserted, updated, or removed.
     */
    private void upsertEndereco(Supplier<Endereco> getter,
                                Consumer<Endereco> setter,
                                Endereco incoming) {
        Endereco current = getter.get();
        // remover
        if (incoming == null) {
            if (current != null) setter.accept(null); // orphanRemoval apaga
            return;
        }

        Long inId = incoming.getId();
        // substituir por existente (id) ou atualizar in place
        if (inId != null) {
            Endereco target = (current != null && inId.equals(current.getId()))
                    ? current
                    : entityManager.getReference(Endereco.class, inId);

            patchEndereco(target, incoming);
            if (target != current) setter.accept(target);
            return;
        }

        // criar novo
        Endereco created = new Endereco();
        patchEndereco(created, incoming);
        setter.accept(created);
    }

    /**
     * Updates the target Endereco object with values from the source Endereco object.
     *
     * @param target the Endereco object to be updated
     * @param src    the Endereco object providing the
     */
    private void patchEndereco(Endereco target, Endereco src) {
        target.setLocal(src.getLocal());
        target.setAldeia(src.getAldeia());
    }

    /**
     * Sets a new value using the provided setter only if the new value is different from the current value.
     * The equality of the current and new values is determined using {@link Objects#equals(Object, Object)}.
     *
     * @param <T>     the type of the current and next values
     * @param setter  a {@link Consumer} that accepts and sets the new value
     * @param current the current value to compare
     * @param next    the new value to be set if it is different from the current value
     */
    private static <T> void setIfChanged(Consumer<T> setter, T current, T next) {
        if (!Objects.equals(current, next)) {
            setter.accept(next);
        }
    }
}
