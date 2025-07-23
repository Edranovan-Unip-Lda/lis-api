package tl.gov.mci.lis.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class FileDownloadUploadException extends RuntimeException {
    public FileDownloadUploadException(String message) {
        super(message);
    }
}
