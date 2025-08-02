package tl.gov.mci.lis.models.pagamento;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.dadosmestre.AtividadeEconomica;
import tl.gov.mci.lis.models.documento.Documento;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_fatura")
public class Fatura extends EntityDB {

    @Enumerated(EnumType.STRING)
    private FaturaStatus status;
    private String nomeEmpresa;
    private String sociedadeComercial;
    private String nif;
    private String sede;
    @Enumerated(EnumType.STRING)
    private NivelRisco nivelRisco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atividade_declarada_id", referencedColumnName = "id")
    private AtividadeEconomica atividadeDeclarada;

    private Double superficie;

    private Double total;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "fatura_taxa",
            joinColumns = @JoinColumn(name = "fatura_id"),
            inverseJoinColumns = @JoinColumn(name = "taxa_id")
    )
    private Set<Taxa> taxas = new HashSet<>();


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pedido_inscricao_cadastro_id", referencedColumnName = "id")
    private PedidoInscricaoCadastro pedidoInscricaoCadastro;

    @OneToOne(mappedBy = "fatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Documento recibo;

    public void addTaxa(Taxa t) {
        taxas.add(t);
        t.getFaturas().add(this);
    }

    public void removeTaxa(Taxa t) {
        taxas.remove(t);
        t.getFaturas().remove(this);
    }

    @Override
    public String toString() {
        return "Fatura{" +
                "status=" + status +
                ", nomeEmpresa='" + nomeEmpresa + '\'' +
                ", sociedadeComercial='" + sociedadeComercial + '\'' +
                ", nif='" + nif + '\'' +
                ", sede='" + sede + '\'' +
                ", nivelRisco=" + nivelRisco +
                ", superficie=" + superficie +
                ", total=" + total +
                '}';
    }
}
