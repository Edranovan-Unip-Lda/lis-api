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
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteRequestDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.CertificadoMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.dtos.vistoria.PedidoVistoriaDto;
import tl.gov.mci.lis.enums.*;
import tl.gov.mci.lis.exceptions.BadRequestException;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.aplicante.HistoricoEstadoAplicanteRepository;
import tl.gov.mci.lis.repositories.atividade.CertificadoLicencaAtividadeRepository;
import tl.gov.mci.lis.repositories.cadastro.CertificadoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.dadosmestre.DirecaoRepository;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.atividade.PedidoLicencaAtividadeService;
import tl.gov.mci.lis.services.authorization.AuthorizationService;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;
import tl.gov.mci.lis.services.endereco.EnderecoService;
import tl.gov.mci.lis.services.user.UserServices;
import tl.gov.mci.lis.services.vistoria.PedidoVistoriaService;

@Service
@RequiredArgsConstructor
public class EmpresaService {
    private static final Logger logger = LoggerFactory.getLogger(EmpresaService.class);
    private final EmpresaRepository empresaRepository;
    private final UserServices userServices;
    private final AplicanteRepository aplicanteRepository;
    private final EnderecoService enderecoService;
    private final UserRepository userRepository;
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

    @Transactional
    public Empresa create(Empresa obj) {
        logger.info("Criando empresa: {}", obj);
        // Register the account first
        obj.getUtilizador().setRole(roleRepository.getReferenceById(obj.getUtilizador().getRole().getId())); // 3 = empresa
        obj.setUtilizador(userServices.register(obj.getUtilizador()));
        obj.setSede(enderecoService.create(obj.getSede()));
        obj.getAcionistas().forEach(obj::addAcionista);
        entityManager.persist(obj);
        return obj;
    }

    public Empresa update(Empresa obj) {
        logger.info("Atualizando empresa: {}", obj);
        Empresa empresa = empresaRepository.findById(obj.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));

        empresa.setSede(enderecoService.update(obj.getSede()));

        if (obj.getUtilizador().getId() == null) {
            throw new BadRequestException("ID do utilizador é obrigatório");
        }

        User utilizador = userRepository.findById(obj.getUtilizador().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado"));
        utilizador.setId(obj.getUtilizador().getId());
        utilizador.setFirstName(obj.getUtilizador().getFirstName());
        utilizador.setLastName(obj.getUtilizador().getLastName());
        utilizador.setEmail(obj.getUtilizador().getEmail());
        empresa.setUtilizador(utilizador);

        empresa.setNome(obj.getNome());
        empresa.setNif(obj.getNif());
        empresa.setGerente(obj.getGerente());
        empresa.setNumeroRegistoComercial(obj.getNumeroRegistoComercial());
        empresa.setTelefone(obj.getTelefone());
        empresa.setTelemovel(obj.getTelemovel());

        return empresaRepository.save(empresa);
    }

    public Empresa getById(Long id) {
        logger.info("Obtendo empresa pelo id: {}", id);
        return empresaRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada"));
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
}
