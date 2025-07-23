package tl.gov.mci.lis.dtos.aplicante;

import lombok.Value;
import tl.gov.mci.lis.enums.AplicanteStatus;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link tl.gov.mci.lis.models.aplicante.Aplicante}
 */
@Value
public class AplicantePageDto implements Serializable {
    Long id;
    Boolean isDeleted;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
    String tipo;
    String categoria;
    String numero;
    AplicanteStatus estado;
    EmpresaDto empresa;

    /**
     * DTO for {@link tl.gov.mci.lis.models.empresa.Empresa}
     */
    @Value
    public static class EmpresaDto implements Serializable {
        Long id;
        String nome;
        String nif;
        String gerente;
        String numeroRegistoComercial;
    }
}