package tl.gov.mci.lis.dtos.pagamento;

import lombok.Value;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.documento.Documento}
 */
@Value
public class DocumentoDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String nome;
    String caminho;
    String extensao;
    String descricao;
    String tipo;
    Long tamanho;
}