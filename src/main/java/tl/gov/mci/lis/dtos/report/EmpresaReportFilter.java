package tl.gov.mci.lis.dtos.report;

import lombok.Data;
import tl.gov.mci.lis.enums.TipoPropriedade;
import tl.gov.mci.lis.enums.cadastro.TipoEmpresa;

import java.time.LocalDate;

/**
 * Filter DTO for Empresa report generation.
 * All fields are optional - only non-null values will be applied as filters.
 */
@Data
public class EmpresaReportFilter {
    private String nome;
    private String nif;
    private String numeroRegistoComercial;
    private String telefone;
    private String telemovel;
    private String email;
    private TipoPropriedade tipoPropriedade;
    private TipoEmpresa tipoEmpresa;
    private LocalDate dataRegistoFrom;
    private LocalDate dataRegistoTo;
    private Double capitalSocialMin;
    private Double capitalSocialMax;
    private Long totalTrabalhadoresMin;
    private Long totalTrabalhadoresMax;
    private Long sociedadeComercialId;
}
