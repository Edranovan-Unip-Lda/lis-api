package tl.gov.mci.lis.models.pagamento;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Getter
@Setter
@Table(name = "lis_taxa")
public class Taxa extends EntityDB {

	private String ato;

	private double montante;

	private String categoria;

}
