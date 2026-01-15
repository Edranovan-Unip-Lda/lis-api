package tl.gov.mci.lis.services.recaptcha;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import tl.gov.mci.lis.dtos.recaptcha.RecaptchaVerifyResponse;
import tl.gov.mci.lis.exceptions.RecaptchaException;

@Service
public class RecaptchaService {
    private static final Logger logger = LoggerFactory.getLogger(RecaptchaService.class);
    private final WebClient webClient;

    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.min-score:0.5}")
    private double minScore;

    @Value("${recaptcha.expected-hostname:}")
    private String expectedHostname;

    @Value("${recaptcha.enabled:true}")
    private boolean enabled;

    public RecaptchaService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://www.google.com/recaptcha/api")
                .build();
    }

    public void validateOrThrow(String token, String expectedAction, String remoteIp) {
        logger.info("Validando recaptcha...");
        if (!enabled) return;

        if (token == null || token.isBlank()) {
            logger.error("Recaptcha Token is null or blank");
            throw new RecaptchaException("Missing reCAPTCHA token");
        }

        RecaptchaVerifyResponse resp = verify(token, remoteIp);

        if (!resp.success()) {
            logger.error("Recaptcha verify failed, error codes: {}", resp.errorCodes());
            throw new RecaptchaException("reCAPTCHA failed: " + resp.errorCodes());
        }

        // Optional hardening: hostname check
        if (expectedHostname != null && !expectedHostname.isBlank()
                && resp.hostname() != null
                && !expectedHostname.equalsIgnoreCase(resp.hostname())) {
            logger.error("Recaptcha verify failed, hostname mismatch");
            throw new RecaptchaException("reCAPTCHA hostname mismatch");
        }

        // v3 checks (score + action). v2 typically doesn't provide score/action.
        if (resp.score() != null) {
            if (resp.score() < minScore) {
                logger.error("Recaptcha verify failed, score mismatch");
                throw new RecaptchaException("reCAPTCHA score too low: " + resp.score());
            }
        }
        if (expectedAction != null && !expectedAction.isBlank() && resp.action() != null) {
            if (!expectedAction.equals(resp.action())) {
                logger.error("Recaptcha verify failed, action mismatch");
                throw new RecaptchaException("reCAPTCHA action mismatch");
            }
        }
    }

    private RecaptchaVerifyResponse verify(String token, String remoteIp) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secret);
        form.add("response", token);
        // remoteip is optional
        if (remoteIp != null && !remoteIp.isBlank()) {
            form.add("remoteip", remoteIp);
        }

        return webClient.post()
                .uri("/siteverify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(form))
                .retrieve()
                .bodyToMono(RecaptchaVerifyResponse.class)
                .block();
    }
}
