package tl.gov.mci.lis.services.vistoria;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.licenca.PedidoLicencaAtividadeDto;
import tl.gov.mci.lis.dtos.mappers.VistoriaMapper;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaDto;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.ClasseAtividadeRepository;
import tl.gov.mci.lis.repositories.endereco.EnderecoRepository;
import tl.gov.mci.lis.repositories.vistoria.PedidoVistoriaRepository;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;

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
    private final EnderecoRepository enderecoRepository;
    private final AplicanteRepository aplicanteRepository;
    private final PedidoInscricaoCadastroService pedidoInscricaoCadastroService;
    private final ClasseAtividadeRepository classeAtividadeRepository;
    private final EntityManager entityManager;
    private final VistoriaMapper vistoriaMapper;

    @Transactional
    public PedidoVistoria create(Long aplicanteId, PedidoVistoria obj) {
        logger.info("Criando pedido de vistoria com aplicanteId: {}", aplicanteId);
        obj.setAplicante(aplicanteRepository.getReferenceById(aplicanteId));
        obj.setClasseAtividade(classeAtividadeRepository.getReferenceById(obj.getClasseAtividade().getId()));
        obj.setStatus(PedidoStatus.SUBMETIDO);
        entityManager.persist(obj);
        return obj;
    }

    @Transactional
    public PedidoVistoria update(Long pedidoId, Long aplicanteId, PedidoVistoria obj) {
        logger.info("Atualizando pedido de vistoria com id: {}", pedidoId);
        PedidoVistoria entity = pedidoVistoriaRepository.findByIdAndAplicante_id(pedidoId, aplicanteId)
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
    public Set<PedidoVistoriaDto> getByAplicanteId(Long aplicanteId) {
        logger.info("Obtendo Pedido Vistoria pelo Id: {}", aplicanteId);

        return pedidoVistoriaRepository.findByAplicante_id(aplicanteId)
                .stream()
                .map(vistoriaMapper::toDto).collect(Collectors.toSet());
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
