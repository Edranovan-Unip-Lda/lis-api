package tl.gov.mci.lis.dtos.vistoria;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.vistoria.Participante}
 */
@Value
public class ParticipanteDto implements Serializable {
    Long id;
    Instant updatedAt;
    String nome;
    String areaRepresentante;
    String cargo;


}