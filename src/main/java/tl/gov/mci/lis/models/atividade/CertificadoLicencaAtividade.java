package tl.gov.mci.lis.models.atividade;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.documento.Documento;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Table(name = "lis_atividade_certificado_licenca")
@Getter
@Setter
public class CertificadoLicencaAtividade extends EntityDB {
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_licenca_atividade_id", referencedColumnName = "id")
    private PedidoLicencaAtividade pedidoLicencaAtividade;

    @NotNull
    private String sociedadeComercial;
    private String tipoSociedadeComercial;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assinatura_id", referencedColumnName = "id")
    private Documento assinatura;
}
