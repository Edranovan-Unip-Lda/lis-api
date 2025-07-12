package tl.gov.mci.lis.dtos.endereco;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.endereco.PostoAdministrativo}
 */
@Value
public class PostoAdministrativoDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String nome;
}