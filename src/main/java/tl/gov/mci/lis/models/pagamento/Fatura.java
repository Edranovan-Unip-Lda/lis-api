package tl.gov.mci.lis.models.pagamento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;

@Entity
@Getter
@Setter
@Table(name = "lis_fatura")
public class Fatura extends EntityDB {

    @Enumerated(EnumType.STRING)
    private FaturaStatus status;

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

    @Override
    public String toString() {
        return "Fatura{" +
                "id=" + getId() +
                ", status=" + status +
                ", atoFatura=" + atoFatura +
                ", nomeEmpresa='" + nomeEmpresa + '\'' +
                ", sociedadeComercial='" + sociedadeComercial + '\'' +
                ", atividadeDeclarada='" + atividadeDeclarada + '\'' +
                ", atividadeDeclaradaCodigo='" + atividadeDeclaradaCodigo + '\'' +
                ", taxaId=" + taxa.getId() +
                ", taxaMontante=" + taxa.getMontante() +
                '}';
    }
}
