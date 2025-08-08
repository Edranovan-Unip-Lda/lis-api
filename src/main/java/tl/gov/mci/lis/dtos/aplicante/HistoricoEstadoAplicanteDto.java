package tl.gov.mci.lis.dtos.aplicante;

import lombok.AllArgsConstructor;
import lombok.Value;
import tl.gov.mci.lis.enums.AplicanteStatus;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante}
 */
@Value
@AllArgsConstructor
public class HistoricoEstadoAplicanteDto implements Serializable {
    Long id;
    AplicanteStatus status;
    String descricao;
    String alteradoPor;
    Instant dataAlteracao;
}