package tl.gov.mci.lis.models.documento;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.pagamento.Fatura;

@Entity
@Table(name = "lis_documento")
@Getter
@Setter
public class Documento extends EntityDB {
    private String nome;
    private String caminho;
    private String extensao;
    private String descricao;
    private String tipo;
    private Long tamanho;

    @OneToOne()
    @JoinColumn(name = "fatura_id", unique = true)
    private Fatura fatura;

    @Override
    public String toString() {
        return "Documento{" +
                "nome='" + nome + '\'' +
                ", caminho='" + caminho + '\'' +
                ", extensao='" + extensao + '\'' +
                ", descricao='" + descricao + '\'' +
                ", tipo='" + tipo + '\'' +
                ", tamanho=" + tamanho +
                '}';
    }
}
