package tl.gov.mci.lis.models.aplicante;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Getter
@Setter
@Table(name = "lis_aplicante_number")
public class AplicanteNumber extends EntityDB {
    private String categoriaCode;
    private int month;
    private int year;

    @Column(unique = true)
    private String formattedCode;

    @Override
    public String toString() {
        return "AplicanteNumber{" +
                "id='" + getId() + '\'' +
                ", categoriaCode='" + categoriaCode + '\'' +
                ", month=" + month +
                ", year=" + year +
                ", formattedCode='" + formattedCode + '\'' +
                '}';
    }
}
