package tl.gov.mci.lis.models.aplicante;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.FaturaStatus;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.dadosmestre.Direcao;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.models.user.User;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "lis_aplicante")
public class Aplicante extends EntityDB {
    private String tipo;

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
    private PedidoInscricaoCadastro pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direcao_id")
    @JsonIgnoreProperties("aplicantesAtribuidos")
    private Direcao direcaoAtribuida;

    public Aplicante() {
    }

    public Aplicante(Long id, Long empresaId, String empresaNome, String empresaNif, AplicanteStatus estado, String numero, Categoria categoria, String tipo, Instant createdAt, Instant updatedAt) {
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
        return (pedido != null) ? pedido.getStatus() : null;
    }

    public FaturaStatus getFaturaStatus() {
        return (pedido != null && pedido.getFatura() != null)
                ? pedido.getFatura().getStatus()
                : null;
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
