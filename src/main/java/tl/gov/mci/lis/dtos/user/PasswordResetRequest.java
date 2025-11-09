package tl.gov.mci.lis.dtos.user;

import lombok.Value;

@Value
public class PasswordResetRequest {
    String token;
    String newPassword;
}
