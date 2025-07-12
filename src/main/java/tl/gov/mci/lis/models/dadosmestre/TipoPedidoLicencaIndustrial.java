package tl.gov.mci.lis.models.dadosmestre;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Getter
@Setter
@Table(name = "lis_dm_tipo_pedido_licenca_industrial")
public class TipoPedidoLicencaIndustrial extends EntityDB {
    String nome;
}
