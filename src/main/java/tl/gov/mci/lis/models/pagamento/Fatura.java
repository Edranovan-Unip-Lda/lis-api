package tl.gov.mci.lis.models.pagamento;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;

@Entity
@Getter
@Setter
@Table(name = "lis_fatura")
public class Fatura extends EntityDB {

    private double atoFatura;

    private String nomeEmpresa;

    private String sociedadeComercial;

    private String atividadeDeclarada;

    private String atividadeDeclaradaCodigo;

    @OneToOne
    @JoinColumn(name = "taxa_id", referencedColumnName = "id")
    private Taxa taxa;

    @OneToOne
    @JoinColumn(name = "pedido_inscricao_cadastro_id", referencedColumnName = "id")
    private PedidoInscricaoCadastro pedidoInscricaoCadastro;

}
