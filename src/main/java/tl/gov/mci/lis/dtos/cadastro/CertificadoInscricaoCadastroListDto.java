package tl.gov.mci.lis.dtos.cadastro;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tl.gov.mci.lis.dtos.pagamento.DocumentoDto;

import java.io.Serializable;
import java.time.Instant;

/**
 * Lightweight DTO for listing CertificadoInscricaoCadastro in pagination
 */
@Data
@AllArgsConstructor
public class CertificadoInscricaoCadastroListDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

    @NotNull
    String sociedadeComercial;
    @NotNull
    String numeroRegistoComercial;
    @NotNull
    String atividade;
    @NotNull
    String dataValidade;
    @NotNull
    String dataEmissao;
    @NotNull
    String nomeDiretorGeral;

    // Complete address hierarchy
    String sedeLocal;
    String sedeAldeiaNome;
    String sedeSucoNome;
    String sedePostoNome;
    String sedeMunicipioNome;

    @NotNull
    String aplicanteNumero;
}


