package tl.gov.mci.lis.services.user;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.configs.email.EmailService;
import tl.gov.mci.lis.configs.jwt.JwtSessionService;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.cadastro.PedidoInscricaoCadastroDto;
import tl.gov.mci.lis.dtos.mappers.EmpresaMapper;
import tl.gov.mci.lis.enums.AccountStatus;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.EmailTemplate;
import tl.gov.mci.lis.enums.Role;
import tl.gov.mci.lis.exceptions.AlreadyExistException;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;
import tl.gov.mci.lis.models.endereco.Endereco;
import tl.gov.mci.lis.models.user.CustomUserDetails;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.aplicante.HistoricoEstadoAplicanteRepository;
import tl.gov.mci.lis.repositories.dadosmestre.DirecaoRepository;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.cadastro.PedidoInscricaoCadastroService;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServices {
    private static final Logger logger = LoggerFactory.getLogger(UserServices.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder bcryptEncoder;
    private final AuthenticationManager authenticationManager;
    private final OneTimePasswordService oneTimePasswordService;
    private final JwtUtil jwtUtil;
    private final JwtSessionService jwtSessionService;
    private final EmailService emailService;
    private final UserDetailServiceImpl userDetailService;
    private final EmpresaRepository empresaRepository;
    private final EntityManager entityManager;
    private final DirecaoRepository direcaoRepository;
    private final AplicanteRepository aplicanteRepository;
    private final AplicanteService aplicanteService;
    private final EmpresaMapper empresaMapper;
    private final PedidoInscricaoCadastroService pedidoInscricaoCadastroService;
    private final HistoricoEstadoAplicanteRepository historicoEstadoAplicanteRepository;

    @Transactional
    public User register(User obj) {
        logger.info("Registering user: {}", obj);

        if (userRepository.findByUsernameOrEmail(obj.getUsername(), obj.getEmail()).isPresent()) {
            throw new AlreadyExistException("User with username " + obj.getUsername() + " or email " + obj.getEmail() + " already exists");
        }

        obj.setRole(
                roleRepository.getReferenceById(obj.getRole().getId())
        );

        if (Objects.nonNull(obj.getDirecao()) && Objects.nonNull(obj.getDirecao().getId())) {
            obj.setDirecao(
                    direcaoRepository.getReferenceById(obj.getDirecao().getId())
            );
        }

        obj.setPassword(bcryptEncoder.encode(obj.getPassword()));

        obj.setStatus(AccountStatus.pending.toString());

        entityManager.persist(obj);
        entityManager.flush();

        emailService.sendEmail(
                userRepository.findByEmail(obj.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("utilizador com email " + obj.getEmail() + " nao existe")),
                EmailTemplate.ACTIVATION
        );
        return obj;
    }

    public User authenticate(String username, String password, HttpServletRequest request) {
        logger.info("Authenticating user: {}", username);

        User user;
        if (this.isValidEmail(username)) {
            String finalUsername = username;
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
//                        auditService.saveLogin(finalUsername, request, false);
                        logger.error("Email ou palavra-passe inválidos");
                        return new ForbiddenException("Email ou palavra-passe inválidos");
                    });
            username = user.getUsername();
        } else {
            String finalUsername1 = username;
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
//                        auditService.saveLogin(finalUsername1, request, false);
                        logger.error("Nome de utilizador ou palavra-passe inválidos");
                        return new ForbiddenException("Nome de utilizador ou palavra-passe inválidos");
                    });
        }
        if (user.getStatus().equals(AccountStatus.disabled.toString())) {
//            auditService.saveLogin(username, request, false);
            logger.error("A sua conta foi desativada. Por favor, contacte o administrador para obter assistência.");
            throw new ForbiddenException("A sua conta foi desativada. Por favor, contacte o administrador para obter assistência.");
        }

        if (user.getStatus().equals(AccountStatus.pending.toString())) {
//            auditService.saveLogin(username, request, false);
            logger.error("É necessária a ativação da conta. Verifique o seu email ou contacte o administrador.");
            throw new ForbiddenException("É necessária a ativação da conta. Verifique o seu email ou contacte o administrador.");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            logger.error("Email/nome de utilizador ou palavra-passe inválidos");
            throw new ForbiddenException("Email/nome de utilizador ou palavra-passe inválidos");
        }

        user.setOneTimePassword(oneTimePasswordService.generateOTP(username));
//        emailService.sendEmail(user, EmailTemplate.OTP);

        logger.info("Successfully login with credential: {}", username);
        return user;
    }

    public User getJWTByOTP(String username, String otp, HttpServletRequest request) {
        logger.info("Validating OTP and getting JWT. Key: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
//                    auditService.saveLogin(username, request, false);
                    logger.error("O código fornecido não é válido ou expirou.");
                    return new ResourceNotFoundException("O código fornecido não é válido ou expirou.");
                });

        if (!oneTimePasswordService.validateOTP(username, otp)) {
            //        auditService.saveLogin(username, request, false);
            logger.error("O código fornecido não é válido ou expirou.");
            throw new ResourceNotFoundException("O código fornecido não é válido ou expirou.");
        }

        user.setJwtSession(jwtUtil.generateToken(username, user.getRole()));

        jwtSessionService.storeActiveToken(username, user.getJwtSession());
        jwtSessionService.storeUserDetails(username, (CustomUserDetails) userDetailService.loadUserByUsername(username));

        if (Role.ROLE_CLIENT.name().equalsIgnoreCase(user.getRole().getName())) {
            user.setEmpresa(empresaRepository.findByUtilizador_Id(user.getId()).orElse(null));
        }
        logger.info("OTP validation is success: {}", username);
//            auditService.saveLogin(username, request, true);
        return user;
    }

    public User resendOTPToEmail(String username) {
        logger.info("Generating new OTP and resending it. Key: {}", username);
        if (oneTimePasswordService.isOtpExistAndValidByKey(username)) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ForbiddenException("Nome de utilizador ou palavra-passe inválidos"));
            user.setOneTimePassword(oneTimePasswordService.getOtpByKey(username));
            emailService.sendEmail(user, EmailTemplate.OTP);
            return user;
        }
        logger.error("A sua sessão expirou. Por favor, inicie sessão novamente.");
        throw new ForbiddenException("A sua sessão expirou. Por favor, inicie sessão novamente.");
    }

    public Page<User> getPage(int page, int size) {
        logger.info("Getting page: {} and size {}", page, size);
        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        Page<User> users = userRepository.getPageBy(paging);
        return new PageImpl<>(users.getContent(), paging, users.getTotalElements());
    }

    public Map<String, String> activateFromEmail(User obj) {
        logger.info("Activate user from token {}", obj.getJwtSession());
        if (jwtUtil.isTokenExpired(obj.getJwtSession())) {
            String username = jwtUtil.extractUsername(obj.getJwtSession());
            return userRepository.findByUsername(username).map(user -> {
                        if (user.getStatus().equals(AccountStatus.pending.toString())) {
                            user.setStatus(obj.getStatus());
                            userRepository.save(user);
                            Map<String, String> responseObj = new HashMap<>();
                            responseObj.put("activation", "true");
                            responseObj.put("message", "O seu email foi verificado e a sua conta está agora ativa. Já pode iniciar sessão e começar a utilizar o sistema.");
                            return responseObj;
                        } else {
                            logger.error("A sua conta já se encontra ativa. Pode iniciar sessão e começar a utilizar o sistema.");
                            throw new AlreadyExistException("A sua conta já se encontra ativa. Pode iniciar sessão e começar a utilizar o sistema.");
                        }
                    })
                    .orElseThrow(() -> {
                        logger.error("User is not found {}", username);
                        return new ResourceNotFoundException("User with username " + username + " not found");
                    });
        } else {
            logger.error("O token é inválido ou expirou.");
            throw new BadCredentialsException("O token é inválido ou expirou.");
        }
    }

    public User getByUsername(String username) {
        logger.info("Getting user with username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found {}", username);
                    return new ResourceNotFoundException("User with username " + username + " not found");
                });
    }

    @Transactional
    public User updateByUsername(String username, User obj) {
        logger.info("Updating user with username: {}", username);

        return userRepository.queryByUsername(username)
                .map(user -> {
                    user.setFirstName(obj.getFirstName());
                    user.setLastName(obj.getLastName());
                    user.setEmail(obj.getEmail());

                    Optional.ofNullable(obj.getPassword())
                            .filter(pw -> !pw.isEmpty())
                            .ifPresent(pw -> user.setPassword(bcryptEncoder.encode(pw)));

                    user.setRole(
                            roleRepository.findById(obj.getRole().getId()).orElseThrow(() -> new ResourceNotFoundException("Role " + obj.getRole().getName() + " not found"))
                    );

                    if (Objects.nonNull(obj.getDirecao()) && Objects.nonNull(obj.getDirecao().getId())) {
                        user.setDirecao(
                                direcaoRepository.getReferenceById(obj.getDirecao().getId())
                        );
                    }

                    user.setStatus(obj.getStatus());
                    return user;
                })

                .orElseThrow(() -> {
                    logger.error("User with username {} not found", username);
                    return new ResourceNotFoundException("User with username " + username + " not found");
                });
    }

    public AplicanteDto getAssignedAplicanteByUsernameAndId(String username, Long aplicanteId) {
        logger.info("Obtendo Aplicante pelo utilizador id: {} e aplicante id: {}", username, aplicanteId);

        User user = userRepository.queryByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Utilizador com {} não existe", username);
                    return new ResourceNotFoundException("Utilizador com o nome  " + username + " não existe");
                });
        AplicanteDto aplicanteDto = aplicanteRepository.getFromIdAndDirecaoId(aplicanteId, user.getDirecao().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));

        empresaRepository.findById(aplicanteDto.getEmpresa().getId())
                .map(empresaMapper::toDto)
                .ifPresent(aplicanteDto::setEmpresa);

        PedidoInscricaoCadastroDto pedidoDto = pedidoInscricaoCadastroService
                .getByAplicanteId(aplicanteDto.getId());
        aplicanteDto.setPedidoInscricaoCadastro(pedidoDto);

        aplicanteDto.setHistoricoStatus(
                historicoEstadoAplicanteRepository.findAllByAplicante_Id(aplicanteDto.getId())
        );
        return aplicanteDto;
    }

    public Page<AplicanteDto> getPageAssignedAplicante(String username, int page, int size) {
        logger.info("Obtendo pagina do Aplicante com nome do utilizador: {}", username);
        User user = userRepository.queryByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Utilizador {} não existe", username);
                    return new ResourceNotFoundException("Utilizador com o nome  " + username + " não existe");
                });

        Pageable paging = PageRequest.of(page, size, Sort.by("id").descending());
        return aplicanteRepository.getPageByDirecaoId(user.getDirecao().getId(), paging);
    }

    @Transactional
    public Aplicante approveAplicante(String username, Long aplicanteId,
                                      HistoricoEstadoAplicante historico) {
        logger.info("Aprovar aplicante: user={}, aplicanteId={}", username, aplicanteId);

        String operador = userRepository.queryByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador " + username + " não existe"))
                .getUsername();

        // Single round-trip, no N+1 when touching associations
        Aplicante aplicante = aplicanteRepository.findByIdWithAllForApproval(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));

        if (aplicante.getEstado() != AplicanteStatus.SUBMETIDO) {
            throw new ForbiddenException("O aplicante não está num estado válido para aprovação.");
        }

        // Histórico
        historico.setAlteradoPor(operador);
        if (historico.getDataAlteracao() == null) {
            historico.setDataAlteracao(Instant.now());
        }
        historico.setStatus(AplicanteStatus.APROVADO); // se existir no modelo
        aplicante.addHistorico(historico); // mantém bi-direcional

        // Atualizações de domínio
        aplicante.setDirecaoAtribuida(null);
        aplicante.setEstado(AplicanteStatus.APROVADO);

        // Copiar a sede do pedido para o certificado (nova entidade endereço)
        Endereco sede = new Endereco();
        Endereco sedePedido = aplicante.getPedidoInscricaoCadastro().getEmpresaSede();
        sede.setLocal(sedePedido.getLocal());
        sede.setAldeia(sedePedido.getAldeia());
        entityManager.persist(sede); // explícito, sem depender de cascade

        // Emitir certificado
        CertificadoInscricaoCadastro cert = new CertificadoInscricaoCadastro();
        cert.setAplicante(aplicante);
        cert.setSociedadeComercial(aplicante.getEmpresa().getNome());
        cert.setNumeroRegistoComercial(aplicante.getEmpresa().getNumeroRegistoComercial());
        cert.setSede(sede);
        cert.setAtividade(aplicante.getPedidoInscricaoCadastro().getClasseAtividade().getDescricao());
        cert.setDataEmissao(LocalDate.now().toString());
        cert.setDataValidade(LocalDate.now().plusYears(2).toString());
        cert.setNomeDiretorGeral("Donald Trump");

        entityManager.persist(cert);
        aplicante.setCertificadoInscricaoCadastro(cert);

        // No save/flush necessário: dirty checking + TX commit
        // historicoStatus e certificado já estão anexados ao 'aplicante' carregado
        return aplicante; // serializa com certificado + historicoStatus
    }

    @Transactional
    public Aplicante rejectAplicante(String username, Long aplicanteId, HistoricoEstadoAplicante historico) {
        logger.info("Rejeitar aplicante com nome do utilizador: {}", username);

        User user = userRepository.queryByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador com o nome " + username + " não existe"));

        Aplicante aplicante = aplicanteRepository.findById(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));

        historico.setStatus(AplicanteStatus.REJEITADO);
        historico.setAplicante(aplicante);
        historico.setAlteradoPor(user.getUsername());

        entityManager.persist(historico);

        aplicante.setEstado(AplicanteStatus.REJEITADO);
        aplicante.setDirecaoAtribuida(null);

        return aplicante; // still managed — changes flushed automatically
    }


    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
