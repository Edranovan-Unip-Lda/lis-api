package tl.gov.mci.lis.models.atividade;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Table(name = "lis_pedido_licenca_atividade_pessoa")
@Getter
@Setter
public class Pessoa extends EntityDB {
    private String nome;
    private String nacionalidade;
    private String naturalidade;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonIgnoreProperties(value = "aplicante")
    private Endereco morada;

    private String telefone;
    private String email;
}
