package tl.gov.mci.lis.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
public class ErrorDetail {
    private Date timeStamp;
    private String message;
    private String details;
    private HttpStatus httpStatus;

    public ErrorDetail() {
    }

}
