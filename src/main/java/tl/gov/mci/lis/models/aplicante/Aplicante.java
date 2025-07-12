package tl.gov.mci.lis.models.aplicante;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.empresa.Empresa;

import java.time.Instant;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "lis_aplicante")
public class Aplicante extends EntityDB {
    private String tipo;
    private String categoria;
    private String numero;
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    @JsonIgnoreProperties(value = "listaAplicante", allowSetters = true)
    private Empresa empresa;

    @OneToMany(mappedBy = "aplicante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "aplicante", allowSetters = true)
    private Set<HistoricoEstadoAplicante> listaHistoricoEstadoAplicante;

    public Aplicante() {
    }

    public Aplicante(Long id, Long empresaId, String empresaNome, String empresaNif, String estado, String numero, String categoria, String tipo, Instant createdAt, Instant updatedAt) {
        this.setId(id);
        this.empresa = new Empresa(empresaId, empresaNome, empresaNif);
        this.estado = estado;
        this.numero = numero;
        this.categoria = categoria;
        this.tipo = tipo;
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
    }
}
