package tl.gov.mci.lis.dtos.empresa;

import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.empresa.Representante}
 */
@Value
public class RepresentanteDto implements Serializable {
    Long id;
    Instant updatedAt;
    String tipo;
    String nomeEmpresa;
    String nome;
    String pai;
    String mae;
    String dataNascimento;
    String estadoCivil;
    String naturalidade;
    String nacionalidade;
    EnderecoDto morada;
    String regiao;
    String tipoDocumento;
    String numeroDocumento;
    String telefone;
    String email;
}