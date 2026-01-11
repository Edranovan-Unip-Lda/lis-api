package tl.gov.mci.lis.models.dadosmestre.atividade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;

import java.util.Set;

@Entity
@Table(name = "lis_dm_atividade_grupo")
@Getter
@Setter
public class GrupoAtividade extends EntityDB {

    @NotBlank
    @Enumerated(EnumType.STRING)
    private Categoria tipo;

    @NotBlank
    private String codigo;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private NivelRisco tipoRisco;

    @OneToMany(mappedBy = "grupoAtividade", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties(value = "grupoAtividade", allowSetters = true)
    private Set<ClasseAtividade> classes;
}
