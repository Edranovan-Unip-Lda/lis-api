package tl.gov.mci.lis.dtos.endereco;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.endereco.Municipio}
 */
@Getter
@Setter
public class MunicipioDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String nome;

    public MunicipioDto(Long id, Boolean isDeleted, Instant createdAt, Instant updatedAt, String createdBy, String updatedBy, String nome) {
        this.id = id;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.nome = nome;
    }
}