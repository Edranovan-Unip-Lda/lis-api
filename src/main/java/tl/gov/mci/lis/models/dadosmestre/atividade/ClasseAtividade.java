package tl.gov.mci.lis.models.dadosmestre.atividade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.enums.cadastro.NivelRisco;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;
import tl.gov.mci.lis.models.cadastro.PedidoInscricaoCadastro;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;
import tl.gov.mci.lis.models.vistoria.Requerente;

import java.util.Set;

@Entity
@Table(name = "lis_dm_atividade_classe")
@Getter
@Setter
public class ClasseAtividade extends EntityDB {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_atividade_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties(value = "classes", allowSetters = true)
    private GrupoAtividade grupoAtividade;

    @NotBlank
    private String codigo;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Categoria tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    private NivelRisco tipoRisco;

    @OneToMany(mappedBy = "classeAtividade")
    @JsonIgnoreProperties(value = "classeAtividade", allowSetters = true)
    private Set<PedidoInscricaoCadastro> listaPedidoInscricaoCadastro;

    @OneToMany(mappedBy = "classeAtividade")
    @JsonIgnoreProperties(value = "classeAtividade", allowSetters = true)
    private Set<PedidoLicencaAtividade> listaPedidoLicencaAtividade;

    @OneToMany(mappedBy = "classeAtividade")
    @JsonIgnoreProperties(value = "classeAtividade", allowSetters = true)
    private Set<PedidoVistoria> listaPedidoVistoria;

    @OneToMany(mappedBy = "classeAtividade")
    @JsonIgnoreProperties(value = "classeAtividade", allowSetters = true)
    private Set<Requerente> listaRequerente;
}
