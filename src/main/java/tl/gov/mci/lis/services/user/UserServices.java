package tl.gov.mci.lis.services.user;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.repository.support.Repositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tl.gov.mci.lis.configs.email.EmailService;
import tl.gov.mci.lis.configs.jwt.JwtSessionService;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.enums.AccountStatus;
import tl.gov.mci.lis.enums.EmailTemplate;
import tl.gov.mci.lis.enums.Role;
import tl.gov.mci.lis.exceptions.AlreadyExistException;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.user.CustomUserDetails;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.dadosmestre.RoleRepository;
import tl.gov.mci.lis.repositories.empresa.EmpresaRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;

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

    @Transactional
    public User register(User obj) {
        logger.info("Registering user: {}", obj);

        if (userRepository.findByUsernameOrEmail(obj.getUsername(), obj.getEmail()).isPresent()) {
            throw new AlreadyExistException("User with username " + obj.getUsername() + " or email " + obj.getEmail() + " already exists");
        }

        obj.setRole(
                roleRepository.getReferenceById(obj.getRole().getId())
        );

        String raw = (obj.getPassword() == null) ? obj.getUsername() : obj.getPassword();
        String encoded = bcryptEncoder.encode(raw);
        obj.setPassword(encoded);

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

    public User updateByUsername(String username, User obj) {
        logger.info("Updating user with username: {}", username);

        User userByUsername = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));
        return userRepository.findById(userByUsername.getId())
                .map(user -> {
                    user.setFirstName(obj.getFirstName());
                    user.setLastName(obj.getLastName());
                    user.setEmail(obj.getEmail());

                    Optional.ofNullable(obj.getPassword())
                            .ifPresent(password -> user.setPassword(bcryptEncoder.encode(password)));

                    obj.setRole(
                            roleRepository.findById(obj.getRole().getId()).orElseThrow(() -> new ResourceNotFoundException("Role " + obj.getRole().getName() + " not found"))
                    );
                    user.setStatus(obj.getStatus());
                    return userRepository.save(user);
                })

                .orElseThrow(() -> {
                    logger.error("User with username {} not found", username);
                    return new ResourceNotFoundException("User with username " + username + " not found");
                });
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
