package tl.gov.mci.lis.dtos.atividade;

import lombok.Value;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.dadosmestre.atividade.GrupoAtividade}
 */
@Value
public class GrupoAtividadeDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    Categoria tipo;
    String codigo;
    String descricao;
    NivelRisco tipoRisco;
}