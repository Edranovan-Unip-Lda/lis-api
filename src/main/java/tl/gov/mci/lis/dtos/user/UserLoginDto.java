package tl.gov.mci.lis.dtos.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import tl.gov.mci.lis.models.dadosmestre.Role;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.user.User}
 */
@Value
public class UserLoginDto implements Serializable {
    Long id;
    String firstName;
    String lastName;
    @NotBlank(message = "Username is mandatory")
    String username;
    String password;
    @NotBlank(message = "Email is mandatory")
    String email;
    RoleDto role;
    String jwtSession;
    String status;
    EmpresaDto empresa;
    DirecaoDto direcao;

    /**
     * DTO for {@link Role}
     */
    @Value
    public static class RoleDto implements Serializable {
        Long id;
        String name;
    }

    /**
     * DTO for {@link tl.gov.mci.lis.models.empresa.Empresa}
     */
    @Value
    public static class EmpresaDto implements Serializable {
        Long id;
        String name;
    }

    /**
     * DTO for {@link tl.gov.mci.lis.models.dadosmestre.Direcao}
     */
    @Value
    public static class DirecaoDto implements Serializable {
        Long id;
        String nome;
    }
}