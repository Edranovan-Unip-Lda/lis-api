package tl.gov.mci.lis.models.vistoria;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Table(name = "lis_atividade_auto_vistoria_requerente")
@Getter
@Setter
public class Requerente extends EntityDB {
    private String denominacaoSocial;
    private String numeroRegistoComercial;

    @OneToOne
    @JoinColumn(name = "sede_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "sede", allowSetters = true)
    private Endereco sede;

    private String nif;
    private String gerente;
    private String telefone;
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_atividade_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties(value = "listaRequerente", allowSetters = true)
    private ClasseAtividade classeAtividade;

    private String nomeRepresentante;
    private String pai;
    private String mae;
    private String dataNascimento;
    private String estadoCivil;
    private String naturalidade;
    private String nacionalidade;

    private String regiao;
    private String tipoDocumento;
    private String numeroDocumento;

    @OneToOne
    @JoinColumn(name = "residencia_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "requerente", allowSetters = true)
    private Endereco residencia;



}
