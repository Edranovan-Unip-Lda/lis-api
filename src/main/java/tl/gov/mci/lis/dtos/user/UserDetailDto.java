package tl.gov.mci.lis.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.dtos.empresa.GerenteDto;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.TipoPropriedade;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;

/**
 * DTO for {@link tl.gov.mci.lis.models.user.User}
 */
@Value
public class UserDetailDto implements Serializable {
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
    @NotBlank(message = "A palavra-passe é obrigatória.")
    String password;
    RoleDto role;
    String status;
    EmpresaDto empresa;
    DirecaoDto direcao;
    DocumentoDto signature;

    /**
     * DTO for {@link tl.gov.mci.lis.models.dadosmestre.Role}
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
        String nome;
        String nif;
        GerenteDto gerente;
        @NotNull
        String numeroRegistoComercial;
        String telefone;
        String telemovel;
        String email;
        @NotNull
        Double capitalSocial;
        @NotNull
        LocalDate dataRegisto;
        @NotNull
        TipoPropriedade tipoPropriedade;
    }

    /**
     * DTO for {@link tl.gov.mci.lis.models.dadosmestre.Direcao}
     */
    @Value
    public static class DirecaoDto implements Serializable {
        Long id;
        Categoria nome;
        String codigo;
    }
}