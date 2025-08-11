package tl.gov.mci.lis.dtos.aplicante;

import lombok.Value;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;

import java.io.Serializable;

/**
 * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
 */
@Value
public class AplicanteReqsDto implements Serializable {
    AplicanteType tipo;
    Categoria categoria;
    EmpresaDto empresa;

    /**
     * DTO for {@link tl.gov.mci.lis.models.empresa.Empresa}
     */
    @Value
    public static class EmpresaDto implements Serializable {
        Long id;
    }
}