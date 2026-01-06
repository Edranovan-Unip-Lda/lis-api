package tl.gov.mci.lis.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for user profile self-update
 */
@Data
public class UserProfileUpdateDto implements Serializable {
    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    // Optional: only if user wants to change password
    private String currentPassword;
    private String newPassword;
}

