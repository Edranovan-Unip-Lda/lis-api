package tl.gov.mci.lis.dtos.pagamento;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import tl.gov.mci.lis.dtos.AtividadeEconomicaDto;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

/**
 * DTO for {@link tl.gov.mci.lis.models.pagamento.Fatura}
 */
@Value
public class FaturaDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    FaturaStatus status;
    String nomeEmpresa;
    String sociedadeComercial;
    String nif;
    String sede;
    NivelRisco nivelRisco;
    AtividadeEconomicaDto atividadeDeclarada;
    Double superficie;
    Double total;
    Set<TaxaDto> taxas;

    /**
     * DTO for {@link tl.gov.mci.lis.models.pagamento.Taxa}
     */
    @Value
    public static class TaxaDto implements Serializable {
        Long id;
        @NotNull
        String ato;
        @NotNull
        Double montanteMinimo;
        @NotNull
        Double montanteMaximo;
        @NotNull
        Categoria categoria;
        @NotNull
        AplicanteType tipo;
    }
}