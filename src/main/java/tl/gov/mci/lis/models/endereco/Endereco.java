package tl.gov.mci.lis.models.endereco;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Table(name = "md_endereco")
@Getter
@Setter
public class Endereco extends EntityDB {

    private String local;

    @ManyToOne(optional = false)  // multiple Endereco per Aldeia
    @JoinColumn(name = "aldeia_id", referencedColumnName = "id")
    private Aldeia aldeia;

    public Endereco() {
    }

    public Endereco(Long id, String local, Long aldeiaId, String aldeiaNome, Long sucoId, String sucoNome, Long postoAdministrativoId, String postoAdministrativoNome, Long municipioId, String municipioNome) {
        this.setId(id);
        this.local = local;
        this.aldeia = new Aldeia(aldeiaId, aldeiaNome, sucoId, sucoNome, postoAdministrativoId, postoAdministrativoNome, municipioId, municipioNome);
    }

    @Override
    public String toString() {
        return "Endereco{" +
                "id=" + getId() + '\'' +
                "local='" + local + '\'' +
                ", aldeia=" + aldeia.getNome() +
                '}';
    }
}
