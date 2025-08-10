package tl.gov.mci.lis.models.cadastro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Getter
@Setter
@Table(name = "lis_cadastro_certificado_inscricao")
public class CertificadoInscricaoCadastro extends EntityDB {

    @OneToOne
    @JoinColumn(name = "aplicante_id", referencedColumnName = "id")
    private Aplicante aplicante;

    @NotNull
    private String sociedadeComercial;

    @NotNull
    private String numeroRegistoComercial;

    @OneToOne
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco sede;

    @NotNull
    private String atividade;

    @NotNull
    private String dataValidade;

    @NotNull
    private String dataEmissao;

    @NotNull
    private String nomeDiretorGeral;

}
