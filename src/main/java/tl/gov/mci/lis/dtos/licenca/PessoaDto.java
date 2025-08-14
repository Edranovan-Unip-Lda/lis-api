package tl.gov.mci.lis.dtos.licenca;

import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.atividade.Pessoa}
 */
@Value
public class PessoaDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String nome;
    String nacionalidade;
    String naturalidade;
    EnderecoDto morada;
    String telefone;
    String email;
}