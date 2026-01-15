package tl.gov.mci.lis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecaptchaException extends RuntimeException {
    public RecaptchaException(String message) {
        super(message);
    }
}
