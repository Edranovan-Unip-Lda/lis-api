package tl.gov.mci.lis.dtos.vistoria;

import lombok.Value;
import tl.gov.mci.lis.dtos.atividade.ClasseAtividadeDto;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;
import tl.gov.mci.lis.dtos.endereco.MunicipioDto;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.vistoria.Requerente}
 */
@Value
public class RequerenteDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant updatedAt;
    String denominacaoSocial;
    String numeroRegistoComercial;
    EnderecoDto sede;
    String nif;
    String gerente;
    String telefone;
    String email;
    ClasseAtividadeDto classeAtividade;
    String nomeRepresentante;
    String pai;
    String mae;
    String dataNascimento;
    String estadoCivil;
    String naturalidade;
    String nacionalidade;
    PostoDto postoAdministrativo;
    String regiao;
    String tipoDocumento;
    String numeroDocumento;
    EnderecoDto residencia;

    @Value
    public static class PostoDto {
        Long id;
        String nome;
        MunicipioDto municipio;
    }
}