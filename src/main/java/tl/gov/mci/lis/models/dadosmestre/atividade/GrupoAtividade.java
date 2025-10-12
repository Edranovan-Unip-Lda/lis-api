package tl.gov.mci.lis.models.dadosmestre.atividade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;

import java.util.Set;

@Entity
@Table(name = "lis_dm_atividade_grupo")
@Getter
@Setter
public class GrupoAtividade extends EntityDB {

    @NonEmpty
    @Enumerated(EnumType.STRING)
    private Categoria tipo;

    @NonEmpty
    private String codigo;

    @NonEmpty
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @NonEmpty
    @Enumerated(EnumType.STRING)
    private NivelRisco tipoRisco;

    @OneToMany(mappedBy = "grupoAtividade", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "grupoAtividade", allowSetters = true)
    private Set<ClasseAtividade> classes;
}
