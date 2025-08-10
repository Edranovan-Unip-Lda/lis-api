package tl.gov.mci.lis.services.empresa;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.dtos.aplicante.AplicanteRequestDto;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.empresa.EmpresaDto;
import tl.gov.mci.lis.dtos.mappers.CertificadoMapper;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.aplicante.HistoricoEstadoAplicanteRepository;
import tl.gov.mci.lis.repositories.cadastro.CertificadoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.cadastro.PedidoInscricaoCadastroRepository;
import tl.gov.mci.lis.repositories.dadosmestre.DirecaoRepository;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.pagamento.FaturaRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.authorization.AuthorizationService;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;
import tl.gov.mci.lis.services.endereco.EnderecoService;
import tl.gov.mci.lis.services.user.UserServices;

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
    private final FaturaRepository faturaRepository;
    private final PedidoInscricaoCadastroRepository pedidoInscricaoCadastroRepository;
    private final DirecaoRepository direcaoRepository;
    private final HistoricoEstadoAplicanteRepository historicoEstadoAplicanteRepository;
    private final CertificadoInscricaoCadastroRepository certificadoInscricaoCadastroRepository;
    private final CertificadoMapper certificadoMapper;

    @Transactional
    public Empresa create(Empresa obj) throws BadRequestException {
        logger.info("Criando empresa: {}", obj);
        // Register the account first
        obj.getUtilizador().setRole(roleRepository.getReferenceById(3L)); // 3 = empresa
        obj.setUtilizador(userServices.register(obj.getUtilizador()));
        obj.setSede(enderecoService.create(obj.getSede()));
        obj.getAcionistas().forEach(obj::addAcionista);
        entityManager.persist(obj);
        return obj;
    }

    public Empresa update(Empresa obj) throws BadRequestException {
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
                    aplicanteDto.setPedidoInscricaoCadastro(pedidoInscricaoCadastroService.getByAplicanteId(aplicanteId));
                    aplicanteDto.setHistoricoStatus(historicoEstadoAplicanteRepository.findAllByAplicante_Id(aplicanteId));
                    if (aplicanteDto.getEstado().equals(AplicanteStatus.APROVADO)) {
                        // Enrich Certificado Cadastro
                        certificadoInscricaoCadastroRepository.findByAplicante_Id(aplicanteDto.getId())
                                .map(certificadoMapper::toDto)
                                .ifPresent(aplicanteDto::setCertificadoInscricaoCadastro);
                    }
                    return aplicanteDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));
    }

    @Transactional
    public Aplicante deleteAplicante(Long empresaId, Long aplicanteId) {
        logger.info("Excluindo aplicante: {}", aplicanteId);

        Aplicante aplicante = aplicanteRepository.findById(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante não encontrado"));

        if (aplicante.getEstado() == AplicanteStatus.SUBMETIDO ||
                aplicante.getEstado() == AplicanteStatus.APROVADO ||
                aplicante.getCertificadoInscricaoCadastro() != null
        ) {
            throw new ForbiddenException("Aplicante deve estar EM_CURSO para ser excluído.");
        }

        // Get Pedido
        PedidoInscricaoCadastro pedido = aplicante.getPedidoInscricaoCadastro();

        if (pedido != null) {
            // Get Fatura
            Fatura fatura = pedido.getFatura();
            if (fatura != null) {
                // Break link to Documento recibo
                Documento recibo = fatura.getRecibo();
                if (recibo != null) {
                    fatura.setRecibo(null); // orphanRemoval triggers deletion
                }

                // Clear many-to-many to avoid constraint violation
                fatura.getTaxas().clear();

                // Break link to Pedido (optional for safety)
                fatura.setPedidoInscricaoCadastro(null);

                // Delete Fatura
                faturaRepository.delete(fatura);
            }

            // Optional: delete Endereco if you don't need it anymore
            pedido.setEmpresaSede(null);
            pedido.setLocalEstabelecimento(null);

            // Break link to Aplicante
            pedido.setAplicante(null);

            // Delete Pedido
            pedidoInscricaoCadastroRepository.delete(pedido);
        }

        // Now safe to delete Aplicante
        Empresa empresa = aplicante.getEmpresa();
        if (empresa != null) {
            empresa.getListaAplicante().remove(aplicante); // break bidirectional
        }

        aplicanteRepository.delete(aplicante);
        return aplicante;
    }

    private static boolean isAplicanteReadyForSubmission(Aplicante aplicante) {
        return (aplicante.getEstado() == AplicanteStatus.EM_CURSO || aplicante.getEstado() == AplicanteStatus.REJEITADO)
                && aplicante.getPedidoInscricaoCadastro().getStatus() == PedidoStatus.SUBMETIDO
                && aplicante.getPedidoInscricaoCadastro().getFatura().getStatus() == FaturaStatus.PAGA;
    }
}
