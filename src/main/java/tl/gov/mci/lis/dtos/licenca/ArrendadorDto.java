package tl.gov.mci.lis.dtos.licenca;

import lombok.Value;
import tl.gov.mci.lis.dtos.endereco.EnderecoDto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link tl.gov.mci.lis.models.atividade.Arrendador}
 */
@Value
public class ArrendadorDto implements Serializable {
    Long id;
    String tipo;
    String nome;
    EnderecoDto endereco;
    String tipoDocumento;
    String numeroDocumento;
    Double areaTotalTerreno;
    Double areaTotalConstrucao;
    LocalDate dataInicio;
    LocalDate dataFim;
    Double valorRendaMensal;
}