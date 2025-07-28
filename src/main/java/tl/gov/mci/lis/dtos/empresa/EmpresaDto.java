package tl.gov.mci.lis.dtos.empresa;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import tl.gov.mci.lis.enums.TipoPropriedade;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO for {@link tl.gov.mci.lis.models.empresa.Empresa}
 */
@Value
public class EmpresaDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String nome;
    String nif;
    UserDto utilizador;
    String gerente;
    String numeroRegistoComercial;
    String telefone;
    String telemovel;
    EnderecoDto sede;
    TipoPropriedade tipoPropriedade;
    Double capitalSocial;
    LocalDate dataRegisto;
    SociedadeComercialDto sociedadeComercial;

    /**
     * DTO for {@link tl.gov.mci.lis.models.user.User}
     */
    @Value
    public static class UserDto implements Serializable {
        Long id;
        Boolean isDeleted;
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
            SucoDto suco;

            @Value
            public static class SucoDto implements Serializable {
                Long id;
                @NotBlank(message = "Nome é obrigatório")
                String nome;
                PostoAdministrativoDto postoAdministrativo;

                @Value
                public static class PostoAdministrativoDto implements Serializable {
                    Long id;
                    @NotBlank(message = "Nome é obrigatório")
                    String nome;
                    MunicipioDto municipio;

                    @Value
                    public static class MunicipioDto implements Serializable {
                        Long id;
                        @NotBlank(message = "Nome é obrigatório")
                        String nome;

                    }

                }

            }
        }
    }

    @Value
    public static class SociedadeComercialDto {
        String nome;
        String acronimo;
    }
}