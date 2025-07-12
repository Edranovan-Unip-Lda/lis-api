package tl.gov.mci.lis.dtos.aplicante;

import lombok.Data;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
 */
@Data
public class AplicanteDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String tipo;
    String categoria;
    String numero;
    String estado;
    Set<HistoricoEstadoAplicanteDto> listaHistoricoEstadoAplicante;

    /**
     * DTO for {@link tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante}
     */
    @Data
    public static class HistoricoEstadoAplicanteDto implements Serializable {
        Long id;
        Boolean isDeleted;
        Instant createdAt;
        Instant updatedAt;
        String createdBy;
        String updatedBy;
        String estadoAnterior;
        String estadoAtual;
    }

}