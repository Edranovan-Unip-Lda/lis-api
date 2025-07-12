package tl.gov.mci.lis.models.aplicante;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Getter
@Setter
@Table(name = "lis_historico_estado_aplicante")
public class HistoricoEstadoAplicante extends EntityDB {
    String estadoAnterior;
    String estadoAtual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aplicante_id", nullable = false)
    @JsonIgnoreProperties(value = "listaHistoricoEstadoAplicante", allowSetters = true)
    Aplicante aplicante;
}
