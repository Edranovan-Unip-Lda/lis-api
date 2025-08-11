package tl.gov.mci.lis.models.aplicante;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.*;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.empresa.Empresa;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "lis_aplicante")
public class Aplicante extends EntityDB {
    @Enumerated(EnumType.STRING)
    private AplicanteType tipo;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    private String numero;
    @Enumerated(EnumType.STRING)
    private AplicanteStatus estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnoreProperties(value = "listaAplicante", allowSetters = true)
    private Empresa empresa;

    @OneToOne(mappedBy = "aplicante")
    private PedidoInscricaoCadastro pedidoInscricaoCadastro;

    @OneToOne(mappedBy = "aplicante")
    private CertificadoInscricaoCadastro certificadoInscricaoCadastro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direcao_id")
    @JsonIgnoreProperties("aplicantesAtribuidos")
    private Direcao direcaoAtribuida;

    @OneToMany(mappedBy = "aplicante", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataAlteracao DESC")
    @JsonIgnoreProperties("aplicante")
    private List<HistoricoEstadoAplicante> historicoStatus = new ArrayList<>();

    public Aplicante() {
    }

    public Aplicante(Long id, Long empresaId, String empresaNome, String empresaNif, AplicanteStatus estado, String numero, Categoria categoria, AplicanteType tipo, Instant createdAt, Instant updatedAt) {
        this.setId(id);
        this.empresa = new Empresa(empresaId, empresaNome, empresaNif);
        this.estado = estado;
        this.numero = numero;
        this.categoria = categoria;
        this.tipo = tipo;
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }

    public PedidoStatus getPedidoStatus() {
        return (pedidoInscricaoCadastro != null) ? pedidoInscricaoCadastro.getStatus() : null;
    }

    public FaturaStatus getFaturaStatus() {
        return (pedidoInscricaoCadastro != null && pedidoInscricaoCadastro.getFatura() != null)
                ? pedidoInscricaoCadastro.getFatura().getStatus()
                : null;
    }

    public void addHistorico(HistoricoEstadoAplicante historico) {
        historico.setAplicante(this);
        historicoStatus.add(historico);
    }

    @Override
    public String toString() {
        return "Aplicante{" +
                "id='" + getId() + '\'' +
                ", tipo='" + tipo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", numero='" + numero + '\'' +
                ", estado=" + estado +
                '}';
    }
}
