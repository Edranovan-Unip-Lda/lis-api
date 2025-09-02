package tl.gov.mci.lis.models.atividade;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Table(name = "lis_atividade_certificado_licenca")
@Getter
@Setter
public class CertificadoLicencaAtividade extends EntityDB {
    @OneToOne
    @JoinColumn(name = "pedido_licenca_atividade_id", referencedColumnName = "id")
    private PedidoLicencaAtividade pedidoLicencaAtividade;

    @NotNull
    private String sociedadeComercial;

    @NotNull
    private String numeroRegistoComercial;

    private String nif;

    @OneToOne
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco sede;

    @NotNull
    private NivelRisco nivelRisco;

    @NotNull
    private String atividade;

    @NotNull
    private String atividadeCodigo;

    @NotNull
    private String dataValidade;

    @NotNull
    private String dataEmissao;

    @NotNull
    private String nomeDiretorGeral;
}
