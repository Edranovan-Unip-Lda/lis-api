package tl.gov.mci.lis.dtos.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import tl.gov.mci.lis.enums.TipoPropriedade;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

/**
 * DTO for {@link tl.gov.mci.lis.models.empresa.Empresa}
 */
@Value
@Getter
@Setter
public class EmpresaRequestDto implements Serializable {
    Long id;
    String nome;
    String nif;
    UserDto utilizador;
    @NotNull
    String gerente;
    @NotNull
    String numeroRegistoComercial;
    String telefone;
    String telemovel;
    @NotNull
    Double capitalSocial;
    @NotNull
    LocalDate dataRegisto;
    @NotNull
    TipoPropriedade tipoPropriedade;
    SociedadeComercialDto sociedadeComercial;
    Set<AcionistaDto> acionistas;
    EnderecoDto sede;

    /**
     * DTO for {@link tl.gov.mci.lis.models.user.User}
     */
    @Value
    @Getter
    @Setter
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
        RoleDto role;

        /**
         * DTO for {@link tl.gov.mci.lis.models.dadosmestre.Role}
         */
        @Value
        @Getter
        @Setter
        public static class RoleDto implements Serializable {
            Long id;
            String name;
        }
    }

    /**
     * DTO for {@link tl.gov.mci.lis.models.dadosmestre.SociedadeComercial}
     */
    @Value
    @Getter
    @Setter
    public static class SociedadeComercialDto implements Serializable {
        Long id;
        @NotNull
        String nome;
        String acronimo;
    }

    /**
     * DTO for {@link tl.gov.mci.lis.models.empresa.Acionista}
     */
    @Value
    @Getter
    @Setter
    public static class AcionistaDto implements Serializable {
        Long id;
        @NotNull
        String nome;
        @NotNull
        String nif;
        @NotNull
        String tipoDocumento;
        @NotNull
        String numeroDocumento;
        @NotNull
        String email;
        @NotNull
        Double acoes;
    }

    /**
     * DTO for {@link tl.gov.mci.lis.models.endereco.Endereco}
     */
    @Value
    @Getter
    @Setter
    public static class EnderecoDto implements Serializable {
        Long id;
        String local;
        AldeiaDto aldeia;

        /**
         * DTO for {@link tl.gov.mci.lis.models.endereco.Aldeia}
         */
        @Value
        @Getter
        @Setter
        public static class AldeiaDto implements Serializable {
            Long id;
            @NotBlank(message = "Nome é obrigatório")
            String nome;
        }
    }
}