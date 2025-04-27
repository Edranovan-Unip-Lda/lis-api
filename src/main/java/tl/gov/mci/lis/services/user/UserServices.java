package tl.gov.mci.lis.services.user;

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
import tl.gov.mci.lis.configs.email.EmailService;
import tl.gov.mci.lis.configs.jwt.JwtSessionService;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.enums.EmailTemplate;
import tl.gov.mci.lis.enums.Status;
import tl.gov.mci.lis.exceptions.AlreadyExistException;
import tl.gov.mci.lis.exceptions.ForbiddenException;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.datamaster.RoleRepository;
import tl.gov.mci.lis.repositories.user.UserRepository;

import java.util.HashMap;
import java.util.Map;
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

    public User register(User obj) {
        logger.info("Registering user: {}", obj);

        if (userRepository.findByUsername(obj.getUsername()).isPresent() || userRepository.findByEmail(obj.getEmail()).isPresent()) {
            throw new AlreadyExistException("User with username " + obj.getUsername() + " or email " + obj.getEmail() + " already exists");
        }

        obj.setRole(
                roleRepository.findById(obj.getRole().getId()).orElseThrow(() -> new ResourceNotFoundException("Role " + obj.getRole().getName() + " not found"))
        );

        obj.setPassword(bcryptEncoder.encode(obj.getPassword()));
        obj.setStatus(Status.pending.toString());

        User savedUser = userRepository.save(obj);
        emailService.sendEmail(
                savedUser,
                EmailTemplate.ACTIVATION
        );
        return savedUser;
    }

    public User authenticate(String username, String password, HttpServletRequest request) {
        logger.info("Authenticating user: {}", username);

        User user;
        if (this.isValidEmail(username)) {
            String finalUsername = username;
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> {
//                        auditService.saveLogin(finalUsername, request, false);
                        logger.error("Invalid email or password");
                        return new ForbiddenException("Invalid email or password");
                    });
            username = user.getUsername();
        } else {
            String finalUsername1 = username;
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> {
//                        auditService.saveLogin(finalUsername1, request, false);
                        logger.error("Invalid username or password");
                        return new ForbiddenException("Invalid username or password");
                    });
        }
        if (user.getStatus().equals(Status.disabled.toString())) {
//            auditService.saveLogin(username, request, false);
            logger.error("Your account has been disabled. Please contact your administrator for assistance.");
            throw new ForbiddenException("Your account has been disabled. Please contact your administrator for assistance.");
        }

        if (user.getStatus().equals(Status.pending.toString())) {
//            auditService.saveLogin(username, request, false);
            logger.error("Account activation required. Check your email or contact admin.");
            throw new ForbiddenException("Account activation required. Check your email or contact admin.");
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (Exception e) {
            logger.error("Invalid email/username or password");
            throw new ForbiddenException("Invalid email/username or password");
        }

        user.setOneTimePassword(oneTimePasswordService.generateOTP(username));
        emailService.sendEmail(user, EmailTemplate.OTP);

        logger.info("Successfully login with credential: {}", username);
        return user;
    }

    public User getJWTByOTP(String username, String otp, HttpServletRequest request) {
        logger.info("Validating OTP and getting JWT. Key: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
//                    auditService.saveLogin(username, request, false);
                    logger.error("The provided code is not valid or has expired.");
                    return new ForbiddenException("The provided code is not valid or has expired.");
                });

        if (!oneTimePasswordService.validateOTP(username, otp)) {
            //        auditService.saveLogin(username, request, false);
            logger.error("The provided code is not valid or has expired.");
            throw new ForbiddenException("The provided code is not valid or has expired.");
        }

        user.setJwtSession(jwtUtil.generateToken(username, user.getRole()));
        jwtSessionService.storeActiveToken(username, user.getJwtSession());

        logger.info("OTP validation is success: {}", username);
//            auditService.saveLogin(username, request, true);
        return user;
    }

    public User resendOTPToEmail(String username) {
        logger.info("Generating new OTP and resending it. Key: {}", username);
        if (oneTimePasswordService.isOtpExistAndValidByKey(username)) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ForbiddenException("Invalid username or password"));
            user.setOneTimePassword(oneTimePasswordService.getOtpByKey(username));
            emailService.sendEmail(user, EmailTemplate.OTP);
            return user;
        }
        logger.error("Your session has expired. Please log in again.");
        throw new ForbiddenException("Your session has expired. Please log in again.");
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
                        if (user.getStatus().equals(Status.pending.toString())) {
                            user.setPassword(bcryptEncoder.encode(obj.getPassword()));
                            user.setStatus(obj.getStatus());
                            userRepository.save(user);
                            Map<String, String> responseObj = new HashMap<>();
                            responseObj.put("activation", "true");
                            responseObj.put("message", "Your account has been successfully activated!");
                            return responseObj;
                        } else {
                            logger.error("The account has already been activated.");
                            throw new AlreadyExistException("The account has already been activated.");
                        }
                    })
                    .orElseThrow(() -> {
                        logger.error("User is not found {}", username);
                        return new ResourceNotFoundException("User with username " + username + " not found");
                    });
        } else {
            logger.error("Token is invalid or expired");
            throw new BadCredentialsException("Token is invalid or expired");
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
