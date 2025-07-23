package tl.gov.mci.lis.models.documento;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Table(name = "lis_documento")
@Getter
@Setter
@ToString
public class Documento extends EntityDB {
    private String nome;
    private String caminho;
    private String extensao;
    private String descricao;
    private String tipo;
    private String tamanho;
}
