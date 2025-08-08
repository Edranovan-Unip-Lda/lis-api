package tl.gov.mci.lis.dtos.cadastro;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro}
 */
@Value
public class CertificadoInscricaoCadastroDto implements Serializable {
    Long id;
    AplicanteDto aplicante;
    @NotNull
    String sociedadeComercial;
    @NotNull
    String numeroRegistoComercial;
    EnderecoDto sede;
    @NotNull
    String atividade;
    @NotNull
    String dataValidade;
    @NotNull
    String dataEmissao;
    @NotNull
    String nomeDiretorGeral;

    /**
     * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
     */
    @Value
    public static class AplicanteDto implements Serializable {
        Long id;
        String numero;
    }
}