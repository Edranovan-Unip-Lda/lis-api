package tl.gov.mci.lis.models.dadosmestre;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Getter
@Setter
@Table(name = "lis_dm_sociedade_comercial")
public class SociedadeComercial extends EntityDB {
    private String nome;
    private String acronimo;;
}
