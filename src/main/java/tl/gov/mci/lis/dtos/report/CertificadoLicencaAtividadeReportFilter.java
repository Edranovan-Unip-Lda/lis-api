package tl.gov.mci.lis.dtos.report;

import lombok.Data;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.CaraterizacaoEstabelecimento;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.enums.cadastro.QuantoAtividade;
import tl.gov.mci.lis.enums.cadastro.TipoAto;
import tl.gov.mci.lis.enums.vistoria.TipoVistoria;

/**
 * Filter DTO for CertificadoLicencaAtividade report generation.
 * All fields are optional - only non-null values will be applied as filters.
 */
@Data
public class CertificadoLicencaAtividadeReportFilter {
    private Categoria categoria;
    private Long empresaId;
    // Certificado Fields
    private Long municipioId;
    private Long postoAdministrativoId;
    private Long sucoId;
    private NivelRisco risco;
    private String dataValidadeFrom;
    private String dataValidadeTo;
    private String dataEmissaoFrom;
    private String dataEmissaoTo;

    // Pedido Licenca Atividade fields
    private Long classeAtividadeId;

    // Pedido Vistoria fields
    private TipoVistoria tipoVistoria;
    private CaraterizacaoEstabelecimento tipoEstabelecimento;
    private TipoAto atividade;
    private QuantoAtividade tipoAtividade;
}
