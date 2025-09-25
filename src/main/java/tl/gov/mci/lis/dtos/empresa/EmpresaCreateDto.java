package tl.gov.mci.lis.dtos.empresa;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.enums.TipoPropriedade;
import tl.gov.mci.lis.enums.cadastro.TipoEmpresa;
import tl.gov.mci.lis.models.dadosmestre.SociedadeComercial;

import java.io.Serializable;
import java.time.LocalDate;

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
    Double capitalSocial;
    String dataRegisto;
    TipoPropriedade tipoPropriedade;
    SociedadeComercialDto sociedadeComercial;
    Long totalTrabalhadores;
    Double volumeNegocioAnual;
    Double balancoTotalAnual;
    TipoEmpresa tipoEmpresa;

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
        @NotBlank(message = "Password is mandatory")
        @NotNull(message = "Password is mandatory")
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

    @Value
    public static class SociedadeComercialDto implements Serializable {
        Long id;
        String nome;
        String acronimo;
    }
}