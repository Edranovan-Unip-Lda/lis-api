package tl.gov.mci.lis.dtos.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import tl.gov.mci.lis.models.datamaster.Role;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.user.User}
 */
@Value
public class UserDto implements Serializable {
    Long id;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    @NotBlank(message = "Firstname is mandatory")
    String firstName;
    @NotBlank(message = "Lastname is mandatory")
    String lastName;
    @NotBlank(message = "Username is mandatory")
    String username;
    @NotBlank(message = "Email is mandatory")
    String email;
    RoleDto role;
    String status;

    /**
     * DTO for {@link Role}
     */
    @Value
    public static class RoleDto implements Serializable {
        Long id;
        String name;
    }
}