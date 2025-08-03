package tl.gov.mci.lis;

import lombok.Value;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade}
 */
@Value
public class ClasseAtividadeDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String codigo;
    String descricao;
    Categoria tipo;
    NivelRisco tipoRisco;
}