package tl.gov.mci.lis.configs.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tl.gov.mci.lis.configs.jwt.JwtUtil;
import tl.gov.mci.lis.enums.EmailTemplate;
import tl.gov.mci.lis.exceptions.ResourceNotFoundException;
import tl.gov.mci.lis.models.user.User;
import tl.gov.mci.lis.repositories.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Service for sending emails.
 * This class is responsible for managing the email sending process, including
 * selecting the appropriate email configuration and generating email content
 * using templates.
 * <p>
 * It utilizes the JavaMailSender for sending emails and Thymeleaf template
 * engine for generating email content.
 * <p>
 * Dependencies:
 * - EmailConfigRepository: for retrieving email configuration settings.
 * - TemplateEngine: for processing email templates.
 * - JwtUtil: for handling JWT-related operations.
 * <p>
 * Configuration:
 * - The frontend URL is injected via @Value annotation.
 * <p>
 * It also contains asynchronous operations to send emails without blocking the
 * main application thread.
 * <p>
 * This class is part of the email configuration package and is annotated with
 *
 * @Service to indicate it's a Spring service component.
 */
@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final EmailConfigRepository emailConfigRepository;
    private final TemplateEngine templateEngine;
    @Value("${frontend.url}")
    private String frontEndURL;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;


    /**
     * Email the given user using the given email template.
     *
     * @param obj           the user to send the email to
     * @param emailTemplate the email template to use
     */
    @Async
    public void sendEmail(User obj, EmailTemplate emailTemplate) {
        logger.info("Enviando email para: {}, {}", obj.getEmail(), obj.getId());

        User user = userRepository.findByEmail(obj.getEmail())
                .orElseThrow(() -> {
                    logger.error("Utilizador nao existe");
                    return new ResourceNotFoundException("Utilizador nao existe");
                });

        try {
            EmailConfig emailConfig = emailConfigRepository.findTopByOrderByIdDesc();
            if (emailConfig == null) {
                throw new IllegalStateException("SMTP settings not found in the database");
            }

            JavaMailSender mailSender = getJavaMailSender(emailConfig);
            String emailContent = generateEmailContent(emailTemplate, user);
            MimeMessage message = prepareMimeMessage(mailSender, emailConfig.getFromEmail(), user, emailTemplate.toString(), emailContent);
            sendMimeMessage(mailSender, message);
            logger.info("Email sent successfully to {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send email to {}", user.getEmail(), e);
        }
    }

    @Async
    public void sendMultipleEmail(User createdBy, List<User> userList, EmailTemplate emailTemplate) {
        try {
            EmailConfig emailConfig = emailConfigRepository.findTopByOrderByIdDesc();
            if (emailConfig == null) {
                throw new IllegalStateException("SMTP settings not found in the database");
            }

            JavaMailSender mailSender = getJavaMailSender(emailConfig);
            Context context = new Context();
            context.setVariable("createdBy", createdBy);
            context.setVariable("insertedDate", Instant.now());
            String emailContent = templateEngine.process(emailTemplate.toString(), context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emailConfig.getFromEmail());

            //Filter only sent to Admin
            helper.setTo(userList.stream().map(User::getEmail).toArray(String[]::new));
            helper.setBcc(userList.stream().map(User::getEmail).toArray(String[]::new));
            helper.setSubject("Alerts");
            helper.setText(emailContent, true);

            sendMimeMessage(mailSender, message);
            logger.info("Email sent successfully to Admins & Staff");
        } catch (Exception e) {
            logger.error("Failed to send email to Admins & Staff", e);
        }
    }

    public Map<String, String> sendTestEmail(String to) {
        EmailConfig emailConfig = emailConfigRepository.findTopByOrderByIdDesc();
        try {
            JavaMailSender mailSender = getJavaMailSender(emailConfig);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Test Email");
            message.setText("This is a test email sent from JavaMailSender.");

            // Optionally set from address if not set globally
            message.setFrom(emailConfig.getFromEmail());

            mailSender.send(message);

            return Map.of("message", String.format("Test email sent to %s", to));
        } catch (Exception e) {
            logger.error("Failed to send email", e);
            throw new ResourceNotFoundException("Mail server connection failed");
        }
    }

    /**
     * Prepare a MimeMessage to be sent using the given {@link JavaMailSender}.
     *
     * @param mailSender   the mail sender to use
     * @param from         the from address to use
     * @param user         the user to send the email to
     * @param subject      the subject of the email
     * @param emailContent the content of the email
     * @return the prepared message
     * @throws Exception if there is an error preparing the message
     */
    private MimeMessage prepareMimeMessage(JavaMailSender mailSender, String from, User user, String subject, String emailContent) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(from);
        helper.setTo(user.getEmail());
        helper.setSubject(subject);
        helper.setText(emailContent, true);
        return message;
    }

    /**
     * Send a prepared MimeMessage using the given {@link JavaMailSender}.
     *
     * @param mailSender the mail sender to use
     * @param message    the prepared message to send
     * @throws Exception if there is an error sending the message
     */
    private void sendMimeMessage(JavaMailSender mailSender, MimeMessage message) throws Exception {
        mailSender.send(message);
    }

    /**
     * Get a {@link JavaMailSender} instance configured with the given
     * {@link EmailConfig}.
     *
     * @param config the email config to use
     * @return the configured mail sender
     */
    private JavaMailSender getJavaMailSender(EmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getSmtpHost());
        mailSender.setPort(config.getSmtpPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(config.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    /**
     * Generate the content of an email using the given template name and user.
     *
     * @param templateName the name of the template to use
     * @param user         the user to include in the email
     * @return the generated email content
     */
    private String generateEmailContent(EmailTemplate templateName, User user) {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("activationLink", frontEndURL + "/auth/activation?t=" + this.jwtUtil.generateToken(user.getUsername(), user.getRole()));
        context.setVariable("loginLink", frontEndURL);
        return templateEngine.process(templateName.toString(), context);
    }
}
