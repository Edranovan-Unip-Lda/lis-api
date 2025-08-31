package tl.gov.mci.lis.dtos.aplicante;

import lombok.Value;
import tl.gov.mci.lis.dtos.user.UserDto;
import tl.gov.mci.lis.enums.EstadoTarefa;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.aplicante.AplicanteAssignment}
 */
@Value
public class AplicanteAssignmentDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant updatedAt;
    AplicanteDto aplicante;
    UserDto assignee;
    UserDto assignedBy;
    Instant assignedAt;
    EstadoTarefa status;
    boolean active;
    String notes;
}