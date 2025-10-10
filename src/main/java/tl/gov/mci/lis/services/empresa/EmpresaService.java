package tl.gov.mci.lis.services.empresa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tl.gov.mci.lis.configs.minio.MinioService;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteRequestDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.CertificadoMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaDto;
import tl.gov.mci.lis.enums.*;
import tl.gov.mci.lis.enums.cadastro.TipoEmpresa;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.empresa.Acionista;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.empresa.Gerente;
import tl.gov.mci.lis.models.empresa.Representante;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.aplicante.HistoricoEstadoAplicanteRepository;
import tl.gov.mci.lis.repositories.atividade.CertificadoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.cadastro.CertificadoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.dadosmestre.DirecaoRepository;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.dadosmestre.SociedadeComercialRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.atividade.PedidoLicencaAtividadeService;
import tl.gov.mci.lis.services.authorization.AuthorizationService;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;
import tl.gov.mci.lis.services.endereco.EnderecoService;
import tl.gov.mci.lis.services.user.UserServices;
import tl.gov.mci.lis.services.vistoria.PedidoVistoriaService;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private static final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final EmpresaRepository empresaRepository;
    private final UserServices userServices;
    private final AplicanteRepository aplicanteRepository;
    private final EnderecoService enderecoService;
    private final RoleRepository roleRepository;
    private final EmpresaMapper empresaMapper;
    private final AuthorizationService authorizationService;
    private final AplicanteService aplicanteService;
    private final PedidoInscricaoCadastroService pedidoInscricaoCadastroService;
    private final EntityManager entityManager;
    private final DirecaoRepository direcaoRepository;
    private final HistoricoEstadoAplicanteRepository historicoEstadoAplicanteRepository;
    private final CertificadoInscricaoCadastroRepository certificadoInscricaoCadastroRepository;
    private final CertificadoMapper certificadoMapper;
    private final PedidoLicencaAtividadeService pedidoLicencaAtividadeService;
    private final PedidoVistoriaService pedidoVistoriaService;
    private final CertificadoLicencaAtividadeRepository certificadoLicencaAtividadeRepository;
    private final MinioService minioService;
    private final SociedadeComercialRepository sociedadeComercialRepository;

    @Transactional
    public Empresa create(Empresa obj, List<MultipartFile> files) {
        logger.info("Criando empresa: {}", obj);
        // Upload the files to Minio
        obj.setDocumentos(minioService.uploadFiles(obj.getUtilizador().getUsername(), files));
        obj.getDocumentos().forEach(documento -> documento.setEmpresa(obj));
        // Register the account first
        obj.getUtilizador().setRole(roleRepository.getReferenceById(obj.getUtilizador().getRole().getId()));
        obj.setUtilizador(userServices.register(obj.getUtilizador()));
        obj.setSede(enderecoService.create(obj.getSede()));
        obj.getAcionistas().forEach(acionista -> {
            obj.addAcionista(acionista);
            acionista.setEndereco(enderecoService.create(acionista.getEndereco()));
        });
        obj.setTipoEmpresa(classificarEmpresa(obj.getTotalTrabalhadores(), obj.getVolumeNegocioAnual(), obj.getBalancoTotalAnual()));
        entityManager.persist(obj);
        return obj;
    }

    @Transactional
    public Empresa update(String username, Empresa incoming) {
        logger.info("Atualizando empresa: {}", username);

        Empresa empresa = empresaRepository.findByUtilizador_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));

        if (empresa.getUtilizador().getRole().getName().equals(Role.ROLE_CLIENT.name())) {
            authorizationService.assertUserOwnsEmpresa(incoming.getId());
        }

        empresa.setSede(enderecoService.update(incoming.getSede()));

        if (incoming.getSociedadeComercial() != null) {
            empresa.setSociedadeComercial(sociedadeComercialRepository.getReferenceById(incoming.getSociedadeComercial().getId()));
        }

        if (incoming.getAcionistas() != null) {
            syncAcionistas(empresa, incoming.getAcionistas());
        }
        if (incoming.getDocumentos() != null) {
            syncDocumentos(empresa, incoming.getDocumentos());
        }
        if (incoming.getGerente() != null) {
            syncGerente(empresa, incoming.getGerente());
        }
        if (incoming.getRepresentante() != null) {
            syncRepresentante(empresa, incoming.getRepresentante());
        }

        setIfChanged(empresa::setNome, empresa.getNome(), incoming.getNome());
        setIfChanged(empresa::setNif, empresa.getNif(), incoming.getNif());
        setIfChanged(empresa::setNumeroRegistoComercial, empresa.getNumeroRegistoComercial(), incoming.getNumeroRegistoComercial());
        setIfChanged(empresa::setTelefone, empresa.getTelefone(), incoming.getTelefone());
        setIfChanged(empresa::setTelemovel, empresa.getTelemovel(), incoming.getTelemovel());
        setIfChanged(empresa::setCapitalSocial, empresa.getCapitalSocial(), incoming.getCapitalSocial());
        setIfChanged(empresa::setDataRegisto, empresa.getDataRegisto(), incoming.getDataRegisto());
        setIfChanged(empresa::setTipoPropriedade, empresa.getTipoPropriedade(), incoming.getTipoPropriedade());
        setIfChanged(empresa::setTotalTrabalhadores, empresa.getTotalTrabalhadores(), incoming.getTotalTrabalhadores());
        setIfChanged(empresa::setVolumeNegocioAnual, empresa.getVolumeNegocioAnual(), incoming.getVolumeNegocioAnual());
        setIfChanged(empresa::setBalancoTotalAnual, empresa.getBalancoTotalAnual(), incoming.getBalancoTotalAnual());
        setIfChanged(empresa::setTipoEmpresa, empresa.getTipoEmpresa(), classificarEmpresa(incoming.getTotalTrabalhadores(), incoming.getVolumeNegocioAnual(), incoming.getBalancoTotalAnual()));
        setIfChanged(empresa::setLatitude, empresa.getLatitude(), incoming.getLatitude());
        setIfChanged(empresa::setLongitude, empresa.getLongitude(), incoming.getLongitude());

        return empresa;
    }

    public Empresa getById(Long id) {
        logger.info("Obtendo empresa pelo id: {}", id);
        return empresaRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
    }

    public Empresa getByUtilizadorUsername(String username) {
        logger.info("Obtendo empresa pelo utilizador: {}", username);
        return empresaRepository.findByUtilizador_Username(username)
                .orElseThrow(() -> {
                    logger.error("Empresa nao encontrada");
                    return new ResourceNotFoundException("Empresa nao encontrada");
                });
    }

    public Page<EmpresaDto> getPageByPageAndSize(int page, int size) {
        logger.info("Obtendo page: {} e size {}", page, size);

        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Empresa> empresas = empresaRepository.findAll(paging);
        return empresas.map(empresaMapper::toDto);
    }

    @Transactional
    public Aplicante createAplicante(Long empresaId, Aplicante obj) {
        logger.info("Criando aplicante: {}", obj);

        authorizationService.assertUserOwnsEmpresa(empresaId);
        obj.setEmpresa(empresaRepository.getReferenceById(empresaId));
        obj.setNumero(aplicanteService.generateAplicanteNumber(obj.getCategoria(), empresaId));
        obj.setEstado(AplicanteStatus.EM_CURSO);

        HistoricoEstadoAplicante historico = new HistoricoEstadoAplicante();
        historico.setStatus(obj.getEstado());
        historico.setAlteradoPor(authorizationService.getCurrentUsername());
        obj.addHistorico(historico);

        entityManager.persist(obj);
        return obj;
    }

    @Transactional
    public Aplicante submitAplicante(Long empresaId, Long aplicanteId, AplicanteRequestDto obj) {
        logger.info("Atualizando aplicante: {}", obj);

        authorizationService.assertUserOwnsEmpresa(empresaId);

        return aplicanteRepository.findByIdAndEmpresa_id(aplicanteId, empresaId)
                .map(aplicante -> {
                    if (!isAplicanteReadyForSubmission(aplicante)) {
                        throw new ForbiddenException("Aplicante deve estar EM_CURSO, pedido SUBMETIDO e fatura PAGA para ser submetido.");
                    }
                    aplicante.setEstado(obj.getEstado());
                    entityManager.merge(aplicante);

                    // Atribuir Aplicante ao Direção
                    Direcao direcao = direcaoRepository.findByNome(aplicante.getCategoria())
                            .orElseThrow(() -> new ResourceNotFoundException("Direção nao encontrada"));

                    HistoricoEstadoAplicante historico = new HistoricoEstadoAplicante();
                    historico.setStatus(aplicante.getEstado());
                    historico.setAlteradoPor(authorizationService.getCurrentUsername());
                    aplicante.addHistorico(historico);

                    aplicanteService.atribuirDirecao(aplicanteId, direcao.getId());

                    return aplicante;
                }).orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));

    }

    public Page<AplicanteDto> getAplicantePage(Long empresaId, int page, int size) {
        logger.info("Obtendo aplicante page pelo empresa id: {}", empresaId);

        authorizationService.assertUserOwnsEmpresa(empresaId);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        return aplicanteRepository.getPageByEmpresaId(empresaId, paging);
    }

    public AplicanteDto getAplicanteById(Long empresaId, Long aplicanteId) {
        logger.info("Obtendo aplicante by id: {}", aplicanteId);

        authorizationService.assertUserOwnsEmpresa(empresaId);
        return aplicanteRepository.getFromIdAndEmpresaId(aplicanteId, empresaId)
                .map(aplicanteDto -> {
                    aplicanteDto.setEmpresa(empresaMapper.toDto(getById(empresaId)));
                    aplicanteDto.setHistoricoStatus(historicoEstadoAplicanteRepository.findAllDtoByAplicante_Id(aplicanteId));

                    switch (aplicanteDto.getTipo()) {
                        case ATIVIDADE -> {
                            aplicanteDto.setPedidoLicencaAtividade(pedidoLicencaAtividadeService.getByAplicanteId(aplicanteId));
                        }
                        case CADASTRO -> {
                            aplicanteDto.setPedidoInscricaoCadastro(pedidoInscricaoCadastroService.getByAplicanteId(aplicanteId));

                            if (aplicanteDto.getEstado().equals(AplicanteStatus.APROVADO)) {
                                // Enrich Certificado Cadastro
                                certificadoInscricaoCadastroRepository.findByPedidoInscricaoCadastro_Id(aplicanteDto.getPedidoInscricaoCadastro().getId())
                                        .map(certificadoMapper::toDto)
                                        .ifPresent(certificadoInscricaoCadastroDto -> aplicanteDto.getPedidoInscricaoCadastro().setCertificadoInscricaoCadastro(certificadoInscricaoCadastroDto));
                            }
                        }
                        default -> {
                            throw new ResourceNotFoundException("Categoria nao encontrada");
                        }
                    }

                    return aplicanteDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));
    }

    @Transactional
    public Long deleteAplicante(Long empresaId, Long aplicanteId) {
        logger.info("Excluindo aplicante: {}", aplicanteId);

        Aplicante aplicante = aplicanteRepository
                .findByIdAndEmpresa_id(aplicanteId, empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante não encontrado"));

        // ---- Business guardrails ----
        if (aplicante.getEstado() == AplicanteStatus.SUBMETIDO ||
                aplicante.getEstado() == AplicanteStatus.APROVADO) {
            throw new ForbiddenException("Aplicante deve estar EM_CURSO para ser excluído.");
        }

        if (aplicante.getPedidoInscricaoCadastro() != null &&
                aplicante.getPedidoInscricaoCadastro().getCertificadoInscricaoCadastro() != null) {
            throw new ForbiddenException("Aplicante com Certificado de Inscrição não pode ser excluído.");
        }

        if (aplicante.getPedidoLicencaAtividade() != null &&
                aplicante.getPedidoLicencaAtividade().getCertificadoLicencaAtividade() != null) {
            throw new ForbiddenException("Aplicante com Certificado de Licença não pode ser excluído.");
        }

        // ---- Clean up associations that cascade won't handle ----
        if (aplicante.getPedidoInscricaoCadastro() != null &&
                aplicante.getPedidoInscricaoCadastro().getFatura() != null) {
            Fatura fatura = aplicante.getPedidoInscricaoCadastro().getFatura();
            if (fatura.getTaxas() != null) {
                fatura.getTaxas().clear(); // Many-to-many won't orphan-remove automatically
            }
        }
        if (aplicante.getPedidoLicencaAtividade() != null &&
                aplicante.getPedidoLicencaAtividade().getFatura() != null) {
            Fatura fatura = aplicante.getPedidoLicencaAtividade().getFatura();
            if (fatura.getTaxas() != null) {
                fatura.getTaxas().clear();
            }
        }

        // ---- Break link to empresa (optional, keeps bidirectional consistency) ----
        Empresa empresa = aplicante.getEmpresa();
        if (empresa != null && empresa.getListaAplicante() != null) {
            empresa.getListaAplicante().remove(aplicante);
            aplicante.setEmpresa(null);
        }

        // ---- Delete root; cascades will remove children (Pedidos, Faturas, Recibos, Certificados) ----
        aplicanteRepository.delete(aplicante);

        logger.info("Aplicante {} removido com sucesso", aplicanteId);
        return aplicanteId;
    }

    @Transactional(readOnly = true)
    public Page<?> getCertificatesPage(Long empresaId, Categoria categoria, AplicanteType aplicanteType, int page, int size) {
        logger.info("Obtendo certificados page pelo empresa id: {}, categoria: {}, type: {}", empresaId, categoria, aplicanteType);
        authorizationService.assertUserOwnsEmpresa(empresaId);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        switch (aplicanteType) {
            case CADASTRO -> {
                return certificadoInscricaoCadastroRepository.findApprovedByEmpresaIdAndCategoria(empresaId, categoria, paging).map(certificadoMapper::toDto1);
            }
            case ATIVIDADE -> {
                return certificadoLicencaAtividadeRepository.findApprovedByEmpresaIdAndCategoria(empresaId, categoria, paging).map(certificadoMapper::toDto1);
            }
            default -> {
                return null;
            }
        }

    }

    private boolean isAplicanteReadyForSubmission(Aplicante aplicante) {
        switch (aplicante.getTipo()) {
            case CADASTRO -> {
                return (aplicante.getEstado() == AplicanteStatus.EM_CURSO || aplicante.getEstado() == AplicanteStatus.REJEITADO)
                        && aplicante.getPedidoInscricaoCadastro().getStatus() == PedidoStatus.SUBMETIDO
                        && aplicante.getPedidoInscricaoCadastro().getFatura().getStatus() == FaturaStatus.PAGA;
            }
            case ATIVIDADE -> {
                PedidoVistoriaDto pedidoVistoria = pedidoVistoriaService
                        .getBypedidoLicencaAtividadeId(aplicante.getPedidoLicencaAtividade().getId()).stream().filter(item -> !item.getStatus().equals(PedidoStatus.REJEITADO)).findFirst().orElse(null);
                if (pedidoVistoria == null) return false;
                return (aplicante.getEstado() == AplicanteStatus.EM_CURSO || aplicante.getEstado() == AplicanteStatus.REJEITADO)
                        && aplicante.getPedidoLicencaAtividade().getStatus() == PedidoStatus.SUBMETIDO
                        && aplicante.getPedidoLicencaAtividade().getFatura().getStatus() == FaturaStatus.PAGA
                        && pedidoVistoria.getStatus() == PedidoStatus.SUBMETIDO
                        && pedidoVistoria.getFatura().getStatus() == FaturaStatus.PAGA;
            }
            default -> {
                return false;
            }
        }

    }

    private static TipoEmpresa classificarEmpresa(Long trabalhadores, double volumeNegocios, double balancoTotal) {
        // MICRO
        if (trabalhadores <= 5 &&
                (volumeNegocios <= 5000 || balancoTotal <= 30000)) {
            return TipoEmpresa.MICROEMPRESA;
        }

        // PEQUENA
        if (trabalhadores >= 6 && trabalhadores <= 20 &&
                (volumeNegocios <= 50000 || balancoTotal <= 200000)) {
            return TipoEmpresa.PEQUENA_EMPRESA;
        }

        // MEDIA
        if (trabalhadores >= 21 && trabalhadores <= 50 &&
                (volumeNegocios <= 1000000 || balancoTotal <= 1240000)) {
            return TipoEmpresa.MEDIA_EMPRESA;
        }

        // GRANDE (mais de 50 trab. ou ultrapassa limites)
        return TipoEmpresa.GRANDE_EMPRESA;
    }

    private void syncGerente(Empresa empresa, Gerente incomingSet) {
        if (empresa.getGerente() == null) {
            empresa.setGerente(incomingSet);
        } else {
            setIfChanged(empresa.getGerente()::setNome, empresa.getGerente().getNome(), incomingSet.getNome());
            setIfChanged(empresa.getGerente()::setMorada, empresa.getGerente().getMorada(), incomingSet.getMorada());
            setIfChanged(empresa.getGerente()::setTelefone, empresa.getGerente().getTelefone(), incomingSet.getTelefone());
            setIfChanged(empresa.getGerente()::setEmail, empresa.getGerente().getEmail(), incomingSet.getEmail());
            setIfChanged(empresa.getGerente()::setTipoDocumento, empresa.getGerente().getTipoDocumento(), incomingSet.getTipoDocumento());
            setIfChanged(empresa.getGerente()::setNumeroDocumento, empresa.getGerente().getNumeroDocumento(), incomingSet.getNumeroDocumento());
        }
    }

    private void syncRepresentante(Empresa empresa, Representante incomingSet) {
        if (empresa.getRepresentante() == null) {
            empresa.setRepresentante(incomingSet);
        } else {
            Representante representante = empresa.getRepresentante();

            if (representante.getMorada() != null) {
                representante.setMorada(enderecoService.update(representante.getMorada()));
            }
            setIfChanged(representante::setTipo, representante.getTipo(), incomingSet.getTipo());
            setIfChanged(representante::setNome, representante.getNome(), incomingSet.getNome());
            setIfChanged(representante::setPai, representante.getPai(), incomingSet.getPai());
            setIfChanged(representante::setMae, representante.getMae(), incomingSet.getMae());
            setIfChanged(representante::setDataNascimento, representante.getDataNascimento(), incomingSet.getDataNascimento());
            setIfChanged(representante::setNacionalidade, representante.getNacionalidade(), incomingSet.getNacionalidade());
            setIfChanged(representante::setNaturalidade, representante.getNaturalidade(), incomingSet.getNaturalidade());
            setIfChanged(representante::setEstadoCivil, representante.getEstadoCivil(), incomingSet.getEstadoCivil());
            setIfChanged(representante::setTipoDocumento, representante.getTipoDocumento(), incomingSet.getTipoDocumento());
            setIfChanged(representante::setNumeroDocumento, representante.getNumeroDocumento(), incomingSet.getNumeroDocumento());
            setIfChanged(representante::setTelefone, representante.getTelefone(), incomingSet.getTelefone());
            setIfChanged(representante::setEmail, representante.getEmail(), incomingSet.getEmail());
        }
    }

    private void syncAcionistas(Empresa empresa, Set<Acionista> incomingSet) {
        if (empresa.getAcionistas() == null) {
            empresa.setAcionistas(new LinkedHashSet<>());
        }

        Map<Long, Acionista> existingById = empresa.getAcionistas().stream()
                .filter(a -> a.getId() != null)
                .collect(Collectors.toMap(Acionista::getId, Function.identity()));

        Set<Long> seenIds = new HashSet<>();

        for (Acionista in : incomingSet) {
            if (in.getId() == null) {
                in.setEmpresa(empresa);
                if (in.getEndereco() != null) {
                    in.setEndereco(in.getEndereco().getId() == null
                            ? enderecoService.create(in.getEndereco())
                            : enderecoService.update(in.getEndereco()));
                }
                empresa.getAcionistas().add(in);
            } else {
                seenIds.add(in.getId());
                Acionista ex = existingById.get(in.getId());
                if (ex == null) {
                    in.setEmpresa(empresa);
                    empresa.getAcionistas().add(in);
                } else {
                    setIfChanged(ex::setNome, ex.getNome(), in.getNome());
                    setIfChanged(ex::setNif, ex.getNif(), in.getNif());
                    setIfChanged(ex::setTipoDocumento, ex.getTipoDocumento(), in.getTipoDocumento());
                    setIfChanged(ex::setNumeroDocumento, ex.getNumeroDocumento(), in.getNumeroDocumento());
                    setIfChanged(ex::setEmail, ex.getEmail(), in.getEmail());
                    setIfChanged(ex::setTelefone, ex.getTelefone(), in.getTelefone());
                    setIfChanged(ex::setAcoes, ex.getAcoes(), in.getAcoes());
                    setIfChanged(ex::setAgregadoFamilia, ex.getAgregadoFamilia(), in.getAgregadoFamilia());
                    setIfChanged(ex::setRelacaoFamilia, ex.getRelacaoFamilia(), in.getRelacaoFamilia());

                    if (in.getEndereco() != null) {
                        ex.setEndereco(in.getEndereco().getId() == null
                                ? enderecoService.create(in.getEndereco())
                                : enderecoService.update(in.getEndereco()));
                    }
                }
            }
        }

        empresa.getAcionistas().removeIf(a -> a.getId() != null && !seenIds.contains(a.getId()));
    }

    private void syncDocumentos(Empresa empresa, List<Documento> incomingList) {
        if (empresa.getDocumentos() == null) {
            empresa.setDocumentos(new ArrayList<>());
        }

        for (Documento in : incomingList) {
            if (in.getId() == null) {
                in.setEmpresa(empresa);
                empresa.getDocumentos().add(in);
            }
        }
    }

    private static <T> void setIfChanged(Consumer<T> setter, T current, T next) {
        if (!Objects.equals(current, next)) {
            setter.accept(next);
        }
    }
}
