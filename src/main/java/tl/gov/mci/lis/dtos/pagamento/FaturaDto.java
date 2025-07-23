package tl.gov.mci.lis.dtos.pagamento;

import lombok.Value;
import tl.gov.mci.lis.enums.FaturaStatus;

import java.io.Serializable;
import java.time.Instant;

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
    double atoFatura;
    String nomeEmpresa;
    String sociedadeComercial;
    String atividadeDeclarada;
    String atividadeDeclaradaCodigo;
    TaxaDto taxa;

    /**
     * DTO for {@link tl.gov.mci.lis.models.pagamento.Taxa}
     */
    @Value
    public static class TaxaDto implements Serializable {
        Long id;
        Boolean isDeleted;
        Instant createdAt;
        Instant updatedAt;
        String createdBy;
        String updatedBy;
        String ato;
        double montante;
        String categoria;
    }
}