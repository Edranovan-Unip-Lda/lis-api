package tl.gov.mci.lis.dtos.empresa;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import tl.gov.mci.lis.enums.TipoPropriedade;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.empresa.Empresa}
 */
@Value
public class EmpresaCreateDto implements Serializable {
    Long id;
    String nome;
    String nif;
    UserDto utilizador;
    String gerente;
    String numeroRegistoComercial;
    String telefone;
    String telemovel;
    EnderecoDto sede;
    String capitalSocial;
    String dataRegisto;
    TipoPropriedade tipoPropriedade;

    /**
     * DTO for {@link tl.gov.mci.lis.models.user.User}
     */
    @Value
    public static class UserDto implements Serializable {
        Long id;
        @NotBlank(message = "Firstname is mandatory")
        String firstName;
        @NotBlank(message = "Lastname is mandatory")
        String lastName;
        @NotBlank(message = "Username is mandatory")
        String username;
        @NotBlank(message = "Email is mandatory")
        String email;
        String password;
    }

    /**
     * DTO for {@link tl.gov.mci.lis.models.endereco.Endereco}
     */
    @Value
    public static class EnderecoDto implements Serializable {
        Long id;
        String local;
        AldeiaDto aldeia;

        /**
         * DTO for {@link tl.gov.mci.lis.models.endereco.Aldeia}
         */
        @Value
        public static class AldeiaDto implements Serializable {
            Long id;
            @NotBlank(message = "Nome é obrigatório")
            String nome;
        }
    }
}