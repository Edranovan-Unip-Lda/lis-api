package tl.gov.mci.lis.models.aplicante;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.models.EntityDB;

import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "lis_aplicante_historico")
public class HistoricoEstadoAplicante extends EntityDB {
    @Enumerated(EnumType.STRING)
    private AplicanteStatus status;

    private String descricao;

    private String alteradoPor;

    private Instant dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aplicante_id", nullable = false)
    @JsonIgnoreProperties(value = "listaHistoricoEstadoAplicante", allowSetters = true)
    Aplicante aplicante;

    @PrePersist
    public void prePersist() {
        this.dataAlteracao = Instant.now();
    }
}
