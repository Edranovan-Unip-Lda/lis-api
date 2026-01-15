package tl.gov.mci.lis.dtos.recaptcha;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;

public record RecaptchaVerifyResponse(
        boolean success,
        @JsonProperty("challenge_ts") Instant challengeTs,
        String hostname,
        Double score,
        String action,
        @JsonProperty("error-codes") List<String> errorCodes
) {
}
