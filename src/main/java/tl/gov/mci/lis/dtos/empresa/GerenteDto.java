package tl.gov.mci.lis.dtos.empresa;

import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.empresa.Gerente}
 */
@Value
public class GerenteDto implements Serializable {
    Long id;
    Instant updatedAt;
    String nome;
    EnderecoDto morada;
    String telefone;
    String email;
    String tipoDocumento;
    String numeroDocumento;
    String naturalidade;
    String nacionalidade;
}