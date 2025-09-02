package tl.gov.mci.lis.models.vistoria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Table(name = "lis_atividade_auto_vistoria_participante")
@Getter
@Setter
public class Participante extends EntityDB {
    private String nome;

    private String areaRepresentante;

    private String cargo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auto_vistoria_id", nullable = false)
    @JsonIgnoreProperties(value = "membrosEquipaVistoria", allowSetters = true)
    private AutoVistoria autoVistoria;


}
