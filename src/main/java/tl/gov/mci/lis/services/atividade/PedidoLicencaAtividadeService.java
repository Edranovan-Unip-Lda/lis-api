package tl.gov.mci.lis.services.atividade;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.licenca.PedidoLicencaAtividadeDto;
import tl.gov.mci.lis.dtos.mappers.FaturaMapper;
import tl.gov.mci.lis.dtos.mappers.LicencaMapper;
import tl.gov.mci.lis.dtos.pagamento.FaturaDto;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.atividade.Arrendador;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;
import tl.gov.mci.lis.models.atividade.Pessoa;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.models.pagamento.Taxa;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.atividade.PedidoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.dadosmestre.atividade.ClasseAtividadeRepository;
import tl.gov.mci.lis.repositories.pagamento.FaturaRepository;
import tl.gov.mci.lis.repositories.pagamento.TaxaRepository;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing operations related to the PedidoLicencaAtividade entity.
 * This includes CRUD operations, transformation to DTOs, and handling of associated entities like Pessoa and Endereco.
 */
@Service
@RequiredArgsConstructor
public class PedidoLicencaAtividadeService {
    private static final Logger logger = LoggerFactory.getLogger(PedidoLicencaAtividadeService.class);
    private final PedidoLicencaAtividadeRepository pedidoLicencaAtividadeRepository;
    private final LicencaMapper licencaMapper;
    private final EntityManager entityManager;
    private final AplicanteRepository aplicanteRepository;
    private final TaxaRepository taxaRepository;
    private final FaturaMapper faturaMapper;
    private final FaturaRepository faturaRepository;
    private final ClasseAtividadeRepository classeAtividadeRepository;

    @Transactional
    public PedidoLicencaAtividade create(Long aplicanteId, PedidoLicencaAtividade reqsObj) {
        logger.info("Criando PedidoLicencaAtividade pelo Aplicante id: {} e PedidoLicencaAtividade: {}", aplicanteId, reqsObj);

        reqsObj.setAplicante(aplicanteRepository.getReferenceById(aplicanteId));
        reqsObj.setClasseAtividade(classeAtividadeRepository.getReferenceById(reqsObj.getClasseAtividade().getId()));
        if (reqsObj.getDocumentos() != null) {
            reqsObj.getDocumentos().forEach(doc -> doc.setPedidoLicencaAtividade(reqsObj));
        }
        reqsObj.setStatus(PedidoStatus.SUBMETIDO);
        entityManager.persist(reqsObj);
        return reqsObj;
    }

    /**
     * Updates an existing PedidoLicencaAtividade entity with the provided data for a specific applicant and order.
     *
     * @param aplicanteId the ID of the applicant associated with the request
     * @param pedidoId    the ID of the order (PedidoLicencaAtividade) to be updated
     * @param incoming    the object containing new data to update the existing entity
     * @return the updated PedidoLicencaAtividade entity
     */
    @Transactional
    public PedidoLicencaAtividade updateByIdAndAplicanteId(Long pedidoId, Long aplicanteId, PedidoLicencaAtividade incoming) {
        logger.info("Atualizando PedidoLicencaAtividade: aplicanteId={},  pedidoId={}, payload={}", aplicanteId, pedidoId, incoming);

        PedidoLicencaAtividade entity = pedidoLicencaAtividadeRepository.findByIdAndAplicante_id(pedidoId, aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("PedidoLicencaAtividade nao encontrado"));


        upsertEndereco(entity::getEmpresaSede, entity::setEmpresaSede, incoming.getEmpresaSede());

        if (incoming.getClasseAtividade() != null) {
            entity.setClasseAtividade(classeAtividadeRepository.getReferenceById(incoming.getClasseAtividade().getId()));
        }

        if (incoming.getDocumentos() != null) {
            incoming.getDocumentos().forEach(doc -> {
                if (Objects.isNull(doc.getId())) {
                    doc.setPedidoLicencaAtividade(entity);
                    entity.getDocumentos().add(doc);
                }
            });
        }

        setIfChanged(entity::setTipo, entity.getTipo(), incoming.getTipo());
        setIfChanged(entity::setNomeEmpresa, entity.getNomeEmpresa(), incoming.getNomeEmpresa());
        setIfChanged(entity::setEmpresaNumeroRegistoComercial, entity.getEmpresaNumeroRegistoComercial(), incoming.getEmpresaNumeroRegistoComercial());
        setIfChanged(entity::setRisco, entity.getRisco(), incoming.getRisco());
        setIfChanged(entity::setEstatutoSociedadeComercial, entity.isEstatutoSociedadeComercial(), incoming.isEstatutoSociedadeComercial());
        setIfChanged(entity::setEmpresaNif, entity.getEmpresaNif(), incoming.getEmpresaNif());
        setIfChanged(entity::setPlanta, entity.isPlanta(), incoming.isPlanta());
        setIfChanged(entity::setDocumentoPropriedade, entity.isDocumentoPropriedade(), incoming.isDocumentoPropriedade());
        setIfChanged(entity::setDocumentoImovel, entity.isDocumentoImovel(), incoming.isDocumentoImovel());
        setIfChanged(entity::setContratoArrendamento, entity.isContratoArrendamento(), incoming.isContratoArrendamento());
        setIfChanged(entity::setPlanoEmergencia, entity.isPlanoEmergencia(), incoming.isPlanoEmergencia());
        setIfChanged(entity::setEstudoAmbiental, entity.isEstudoAmbiental(), incoming.isEstudoAmbiental());
        setIfChanged(entity::setNumEmpregosCriados, entity.getNumEmpregosCriados(), incoming.getNumEmpregosCriados());
        setIfChanged(entity::setNumEmpregadosCriar, entity.getNumEmpregadosCriar(), incoming.getNumEmpregadosCriar());
        setIfChanged(entity::setReciboPagamento, entity.isReciboPagamento(), incoming.isReciboPagamento());
        setIfChanged(entity::setOutrosDocumentos, entity.getOutrosDocumentos(), incoming.getOutrosDocumentos());

        upsertPessoa(entity::getRepresentante, entity::setRepresentante, incoming.getRepresentante());
        upsertPessoa(entity::getGerente, entity::setGerente, incoming.getGerente());
        upsertArrendador(entity::getArrendador, entity::setArrendador, incoming.getArrendador());

        return entity;
    }

    /**
     * Retrieves a PedidoLicencaAtividadeDto object based on the provided applicant ID.
     *
     * @param aplicanteId the ID of the applicant whose PedidoLicencaAtividade is to be retrieved
     * @return a PedidoLicencaAtividadeDto object if a match is found for the given applicant ID, or null if no match exists
     */
    @Transactional(readOnly = true)
    public PedidoLicencaAtividadeDto getByAplicanteId(Long aplicanteId) {
        logger.info("Obtendo Pedido Licenca Atividade pelo Id: {}", aplicanteId);

        return licencaMapper.toDto(
                pedidoLicencaAtividadeRepository.findByAplicante_id(aplicanteId)
                        .orElse(null)
        );
    }

    @Transactional
    public FaturaDto createFatura(Long pedidoId, Fatura obj) {
        logger.info("Criando fatura: {}", obj);
        PedidoLicencaAtividade pedido = pedidoLicencaAtividadeRepository.findDetailById(pedidoId)
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
                .findByIdAndPedidoLicencaAtividade_Id(faturaId, pedidoId)
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
     * Handles the upsert logic for a {@code Pessoa} object, allowing for deletion,
     * updating, or creation of a new {@code Pessoa} instance based on the provided
     * input parameters.
     * <p>
     * This method follows three main operations:
     * 1) Delete: If the incoming {@code Pessoa} is null and a current {@code Pessoa} exists,
     * the current {@code Pessoa} is removed via the provided setter function.
     * 2) Update: If the incoming {@code Pessoa} contains an ID, it attempts to match with
     * the current {@code Pessoa} or fetches a reference to an existing entity. The target
     * {@code Pessoa} is updated (patched) and, if different from the current {@code Pessoa},
     * replaces it via the provided setter function.
     * 3) Create: If the incoming {@code Pessoa} does not have an ID, a new {@code Pessoa} is
     * instantiated, updated (patched), and set as the current instance via the setter function.
     *
     * @param getter   a {@link Supplier} that provides the current {@code Pessoa} object
     * @param setter   a {@link Consumer} that updates the current {@code Pessoa} object
     * @param incoming the incoming {@code Pessoa} object used for deletion, updating, or creation
     */
    private void upsertPessoa(Supplier<Pessoa> getter,
                              Consumer<Pessoa> setter,
                              Pessoa incoming) {
        Pessoa current = getter.get();

        // 1) Remove?
        if (incoming == null) {
            if (current != null) setter.accept(null); // orphanRemoval apaga Pessoa + Endereco
            return;
        }

        Long inId = incoming.getId();

        // 2) Link existing by id (and patch)
        if (inId != null) {
            Pessoa target = (current != null && inId.equals(current.getId()))
                    ? current
                    : entityManager.getReference(Pessoa.class, inId);

            patchPessoa(target, incoming); // inclui morada
            if (target != current) {
                setter.accept(target);     // substitui; orphanRemoval cuida do antigo (se existir)
            }
            return;
        }

        // 3) Create new
        Pessoa created = new Pessoa();
        patchPessoa(created, incoming);     // cria/atualiza morada
        setter.accept(created);             // cascade PERSIST salva tudo
    }

    /**
     * Updates the fields of the target Pessoa object with those from the source Pessoa object.
     * This method selectively patches the target object by copying specific values from the source.
     * Handles associated Morada (Endereco) object with logic for upsert operation.
     *
     * @param target the Pessoa object to be updated
     * @param src    the Pessoa object providing the new values
     */
    private void patchPessoa(Pessoa target, Pessoa src) {
        target.setNome(src.getNome());
        target.setNacionalidade(src.getNacionalidade());
        target.setNaturalidade(src.getNaturalidade());
        target.setTelefone(src.getTelefone());
        target.setEmail(src.getEmail());
        setIfChanged(target::setEstadoCivil, target.getEstadoCivil(), src.getEstadoCivil());

        // Morada (Endereco) — OneToOne cascade + orphanRemoval
        upsertEndereco(target::getMorada, target::setMorada, src.getMorada());
    }

    private void upsertArrendador(
            Supplier<Arrendador> getter,
            Consumer<Arrendador> setter,
            Arrendador incoming
    ) {
        Arrendador current = getter.get();

        // 1) Remove?
        if (incoming == null) {
            if (current != null) setter.accept(null); // orphanRemoval apaga Pessoa + Endereco
            return;
        }

        Long inId = incoming.getId();

        // 2) Link existing by id (and patch)
        if (inId != null) {
            Arrendador target = (current != null && inId.equals(current.getId()))
                    ? current
                    : entityManager.getReference(Arrendador.class, inId);

            patchArrendador(target, incoming); // inclui morada
            if (target != current) {
                setter.accept(target);     // substitui; orphanRemoval cuida do antigo (se existir)
            }
            return;
        }

        // 3) Create new
        Arrendador created = new Arrendador();
        patchArrendador(created, incoming);     // cria/atualiza morada
        setter.accept(created);             // cascade PERSIST salva tudo
    }

    private void patchArrendador(Arrendador target, Arrendador src) {
        target.setNome(src.getNome());
        target.setAreaTotalTerreno(src.getAreaTotalTerreno());
        target.setAreaTotalConstrucao(src.getAreaTotalConstrucao());
        target.setTipoDocumento(src.getTipoDocumento());
        target.setNumeroDocumento(src.getNumeroDocumento());
        target.setDataInicio(src.getDataInicio());
        target.setDataFim(src.getDataFim());
        target.setValorRendaMensal(src.getValorRendaMensal());
        // Morada (Endereco) — OneToOne cascade + orphanRemoval
        upsertEndereco(target::getEndereco, target::setEndereco, src.getEndereco());
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
