package tl.gov.mci.lis.models.empresa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.endereco.Endereco;

@Entity
@Table(name = "lis_empresa_gerentes")
@Getter
@Setter
public class Gerente extends EntityDB {
    private String nome;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "morada_id", referencedColumnName = "id")
    private Endereco morada;

    private String telefone;
    private String email;
    private String tipoDocumento;
    private String numeroDocumento;
}
