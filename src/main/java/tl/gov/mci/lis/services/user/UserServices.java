package tl.gov.mci.lis.services.user;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.configs.email.EmailService;
import tl.gov.mci.lis.configs.jwt.JwtSessionService;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.dtos.user.PasswordResetRequest;
import tl.gov.mci.lis.enums.*;
import tl.gov.mci.lis.exceptions.AlreadyExistException;
import tl.gov.mci.lis.exceptions.BadRequestException;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.user.CustomUserDetails;
import tl.gov.mci.lis.models.user.PasswordResetToken;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.aplicante.AplicanteRepository;
import tl.gov.mci.lis.repositories.dadosmestre.DirecaoRepository;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.user.PasswordResetTokenRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;
import tl.gov.mci.lis.services.aplicante.AplicanteService;
import tl.gov.mci.lis.services.cadastro.CertificadoService;
import tl.gov.mci.lis.services.notificacao.NotificacaoService;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
    private final CertificadoService certificadoService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final NotificacaoService notificacaoService;
    private static final int MAX_ATTEMPTS = 10;

    @Transactional
    public User register(User obj) {
        logger.info("Registering user: {}", obj);

        if (userRepository.findByUsernameOrEmail(obj.getUsername(), obj.getEmail()).isPresent()) {
            throw new AlreadyExistException("O utilizador com o email " + obj.getEmail() + " já existe.");
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
        if (!jwtUtil.isTokenExpired(obj.getJwtSession())) {
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
            logger.error("O token é inválido ou expirado.");
            throw new BadRequestException("O token é inválido ou expirado.");
        }
    }

    @Transactional
    public Map<String, String> sendForgotPasswordEmail(String email) {
        logger.info("Sending forgot password email to {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Utilizador não existe: {}", email);
                    return new ResourceNotFoundException("Utilizador não existe: " + email);
                });

        // Check if user already has a token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUser(user)
                .orElse(new PasswordResetToken());

        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setUser(user);
        resetToken.setExpiryDate(Instant.now().plus(1, ChronoUnit.HOURS));
        entityManager.persist(resetToken);
        emailService.sendForgotPasswordEmail(resetToken);

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("activation", "true");
        responseObj.put("message", "Email enviado. Por favor, verifique a sua caixa de entrada (e a pasta de spam) para a ligação de recuperação.");
        return responseObj;
    }

    @Transactional
    public Map<String, String> resetPassword(PasswordResetRequest request) {
        logger.info("Resetting password with token {}", request.getToken());
        Optional<User> userOpt = this.validatePasswordResetToken(request.getToken());

        if (userOpt.isEmpty()) {
            throw new BadRequestException("Token inválido ou expirado");
        }

        User user = userRepository.findById(userOpt.get().getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Encode the new password before saving
        user.setPassword(bcryptEncoder.encode(request.getNewPassword()));

        // Invalidate token after successful reset
        this.invalidateToken(request.getToken());

        Map<String, String> responseObj = new HashMap<>();
        responseObj.put("message", "Password successfully reset");
        return responseObj;
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

                    // Handle signature update/replacement
                    if (obj.getSignature() != null && obj.getSignature().getId() != null) {
                        // Fetch the managed entity from DB using the ID
                        Documento managedSignature = entityManager.find(Documento.class, obj.getSignature().getId());

                        if (managedSignature != null) {
                            user.setSignature(managedSignature);
                        } else {
                            throw new ResourceNotFoundException("Signature document with ID " + obj.getSignature().getId() + " not found");
                        }
                    } else if (obj.getSignature() == null) {
                        // Remove signature if explicitly set to null
                        user.setSignature(null);
                    }

                    user.setStatus(obj.getStatus());
                    return user;
                })

                .orElseThrow(() -> {
                    logger.error("User with username {} not found", username);
                    return new ResourceNotFoundException("User with username " + username + " not found");
                });
    }

    @Transactional
    public void removeSignature(String username) {
        logger.info("Removing signature with username {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} not found", username);
                    return new ResourceNotFoundException("User with username " + username + " not found");
                });
        user.setSignature(null);
    }

    public Empresa getEmpresaByUtilizadorUsername(String username) {
        return empresaRepository.findByUtilizador_Username(username)
                .orElseThrow(() -> {
                    logger.error("Empresa nao encontrada");
                    return new ResourceNotFoundException("Empresa nao encontrada");
                });
    }

    public Aplicante getAssignedAplicanteByUsernameAndId(String username, Long aplicanteId) {
        logger.info("Obtendo Aplicante pelo utilizador id: {} e aplicante id: {}", username, aplicanteId);

        Aplicante aplicante = aplicanteService.getById(aplicanteId);
        Direcao direcao = userRepository.findDirecaoIdByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Utilizador com {} não existe", username);
                    return new ResourceNotFoundException("Utilizador com o nome  " + username + " não existe");
                });

        if (!direcao.getId().equals(aplicante.getDirecaoAtribuida().getId())) {
            logger.error("Aplicante com id={} nao pertence ao Direcao id={}", aplicanteId, direcao.getId());
            throw new ForbiddenException("Aplicante com id=" + aplicanteId + " nao pertence ao Direcao id=" + direcao.getId());
        }

        return aplicante;
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
    public Aplicante reviewedAplicante(String username, Long aplicanteId) {
        logger.info("Revisto aplicante: user={}, aplicanteId={}", username, aplicanteId);

        User operador = userRepository.queryByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador " + username + " não existe"));
        Aplicante aplicante = aplicanteRepository.findByIdWithAllForApproval(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));

        if (aplicante.getTipo().equals(AplicanteType.CADASTRO) && aplicante.getEstado() != AplicanteStatus.REVISAO) {
            logger.error("O aplicante não está num estado válido para revisao.");
            throw new BadRequestException("O aplicante não está num estado válido para revisao.");
        }

        if (aplicante.getTipo().equals(AplicanteType.ATIVIDADE) && aplicante.getEstado() != AplicanteStatus.REVISAO) {
            logger.error("O aplicante não está num estado válido para revisao.");
            throw new BadRequestException("O aplicante não está num estado válido para revisao.");
        }

        aplicante.setEstado(AplicanteStatus.REVISTO);

        // add history
        HistoricoEstadoAplicante historico = new HistoricoEstadoAplicante();
        if (historico.getDataAlteracao() == null) {
            historico.setDataAlteracao(Instant.now());
        }
        historico.setStatus(aplicante.getEstado());
        historico.setAlteradoPor(operador.getUsername());
        aplicante.addHistorico(historico);

        notificacaoService.createNotification(aplicante.getEmpresa().getUtilizador().getId(), aplicante, EmailTemplate.REVISTO);

        return aplicante;
    }

    @Transactional
    public Aplicante approveAplicante(String username, Long aplicanteId,
                                      HistoricoEstadoAplicante historico) {
        logger.info("Aprovar aplicante: user={}, aplicanteId={}", username, aplicanteId);

        User operador = userRepository.queryByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador " + username + " não existe"));

        // Single round-trip, no N+1 when touching associations
        Aplicante aplicante = aplicanteRepository.findByIdWithAllForApproval(aplicanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Aplicante nao encontrado"));

        if (aplicante.getEstado() != AplicanteStatus.REVISTO) {
            logger.error("O aplicante não está num estado válido para aprovação.");
            throw new BadRequestException("O aplicante não está num estado válido para aprovação.");
        }

        // Histórico
        historico.setAlteradoPor(operador.getUsername());
        if (historico.getDataAlteracao() == null) {
            historico.setDataAlteracao(Instant.now());
        }
        historico.setStatus(AplicanteStatus.APROVADO); // se existir no modelo
        aplicante.addHistorico(historico); // mantém bi-direcional

        // Atualizações de domínio
        aplicante.setDirecaoAtribuida(null);
        aplicante.setEstado(AplicanteStatus.APROVADO);


        // Emitir certificado
        EntityDB cert;

        switch (aplicante.getTipo()) {
            case CADASTRO -> {
                cert = certificadoService.saveCertificadoInscricaoCadastro(aplicante, operador);
                aplicante.getPedidoInscricaoCadastro().setCertificadoInscricaoCadastro((CertificadoInscricaoCadastro) cert);
            }
            case ATIVIDADE -> {
                cert = certificadoService.saveCertificadoLicencaAtividade(aplicante, operador);
                aplicante.getPedidoLicencaAtividade().setCertificadoLicencaAtividade((CertificadoLicencaAtividade) cert);
            }
        }

        notificacaoService.createNotification(aplicante.getEmpresa().getUtilizador().getId(), aplicante, EmailTemplate.APROVADO);
        // No save/flush necessário: dirty checking + TX commit
        // historicoStatus e certificado já estão anexados ao 'aplicante' carregado
        return aplicante; // serializa com certificado + historicoStatus
    }

    @Transactional
    public Aplicante rejectAplicante(String username, Long aplicanteId, HistoricoEstadoAplicante historico) {
        logger.info("Rejeitar aplicante ID={} com nome do utilizador: {}", aplicanteId, username);

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

        switch (aplicante.getTipo()) {
            case CADASTRO -> aplicante.getPedidoInscricaoCadastro().setStatus(PedidoStatus.REJEITADO);
            case ATIVIDADE -> aplicante.getPedidoLicencaAtividade().setStatus(PedidoStatus.REJEITADO);
        }

        notificacaoService.createNotification(aplicante.getEmpresa().getUtilizador().getId(), aplicante, EmailTemplate.REJEITADO);

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

    private Optional<User> validatePasswordResetToken(String token) {
        Optional<PasswordResetToken> resetTokenOpt = passwordResetTokenRepository.findByToken(token);

        if (resetTokenOpt.isEmpty()) {
            return Optional.empty(); // token not found
        }

        PasswordResetToken resetToken = resetTokenOpt.get();

        // Check expiry
        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            return Optional.empty(); // token expired
        }

        return Optional.of(resetToken.getUser());
    }

    /**
     * Optionally invalidate a token after use
     */
    private void invalidateToken(String token) {
        passwordResetTokenRepository.findByToken(token).ifPresent(passwordResetTokenRepository::delete);
    }

    @Transactional
    public User updateOwnProfile(String authenticatedUsername, String targetUsername, String firstName, String lastName, String email, String currentPassword, String newPassword) {
        logger.info("User {} attempting to update profile for {}", authenticatedUsername, targetUsername);

        // Verify that user can only update their own profile
        if (!authenticatedUsername.equals(targetUsername)) {
            logger.error("User {} attempted to update profile for different user {}", authenticatedUsername, targetUsername);
            throw new ForbiddenException("Você só pode atualizar o seu próprio perfil");
        }

        return userRepository.queryByUsername(targetUsername)
                .map(user -> {
                    // Check if email is being changed and if it's already taken by another user
                    if (!user.getEmail().equals(email)) {
                        userRepository.findByEmail(email).ifPresent(existingUser -> {
                            if (!existingUser.getId().equals(user.getId())) {
                                throw new AlreadyExistException("O email " + email + " já está em uso por outro utilizador");
                            }
                        });
                    }

                    // Update basic info
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);

                    // Update password if provided
                    if (currentPassword != null && !currentPassword.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
                        // Verify current password
                        if (!bcryptEncoder.matches(currentPassword, user.getPassword())) {
                            logger.error("Current password verification failed for user {}", targetUsername);
                            throw new BadRequestException("A palavra-passe atual está incorreta");
                        }

                        // Set new password
                        user.setPassword(bcryptEncoder.encode(newPassword));
                        logger.info("Password updated for user {}", targetUsername);
                    }

                    logger.info("Profile updated successfully for user {}", targetUsername);
                    return user;
                })
                .orElseThrow(() -> {
                    logger.error("Username {} not found", targetUsername);
                    return new ResourceNotFoundException("Utilizador com o nome " + targetUsername + " não encontrado");
                });
    }

    public String generateUniqueUsername(String email) {
        String base = normalizeEmailPrefix(email);

        for (int i = 0; i < MAX_ATTEMPTS; i++) {
            String candidate = base + randomSuffix();

            if (!userRepository.existsByUsernameIgnoreCase(candidate)) {
                return candidate;
            }
        }

        // fallback: ultra-unique
        return base + System.currentTimeMillis();
    }

    private String normalizeEmailPrefix(String email) {
        if (email == null || !email.contains("@")) {
            return "user";
        }

        return email.split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }

    private String randomSuffix() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(4);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 4; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
