package tl.gov.mci.lis.models.empresa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Table(name = "lis_empresa_representantes")
@Getter
@Setter
public class Representante extends EntityDB {
    private String tipo;
    private String nome;
    private String pai;
    private String mae;
    private String dataNascimento;
    private String estadoCivil;
    private String naturalidade;
    private String nacionalidade;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "morada_id", referencedColumnName = "id")
    private Endereco morada;
    private String tipoDocumento;
    private String numeroDocumento;
    private String telefone;
    private String email;
}
