package tl.gov.mci.lis.dtos.report;

import lombok.Data;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.*;

import java.time.Instant;

/**
 * Filter DTO for CertificadoInscricaoCadastro report generation.
 * All fields are optional - only non-null values will be applied as filters.
 */
@Data
public class CertificadoInscricaoCadastroReportFilter {
    private Categoria categoria;
    private Long empresaId;
    private TipoEstabelecimento tipoEstabelecimento;
    private CaraterizacaoEstabelecimento caraterizacaoEstabelecimento;
    private NivelRisco risco;
    private TipoAto ato;
    private Long classeAtividadeId;

    private String dataValidadeFrom;
    private String dataValidadeTo;
    private String dataEmissaoFrom;
    private String dataEmissaoTo;

    private Long municipioId;
    private Long postoAdministrativoId;
    private Long sucoId;
}
