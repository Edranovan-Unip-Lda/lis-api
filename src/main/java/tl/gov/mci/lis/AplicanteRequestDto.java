package tl.gov.mci.lis;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.enums.AplicanteStatus;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
 */
@Value
public class AplicanteRequestDto implements Serializable {
    @NotNull
    Long id;
    String tipo;
    @NotNull
    String categoria;
    @NotNull
    String numero;
    @NotNull
    AplicanteStatus estado;
}