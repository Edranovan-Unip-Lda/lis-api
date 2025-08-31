package tl.gov.mci.lis.models.documento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.pagamento.Fatura;
import tl.gov.mci.lis.models.vistoria.AutoVistoria;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_vistoria_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "documentos")
    private AutoVistoria autoVistoria;

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
