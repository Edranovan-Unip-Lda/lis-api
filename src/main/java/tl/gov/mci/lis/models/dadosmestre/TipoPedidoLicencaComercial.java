package tl.gov.mci.lis.models.dadosmestre;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;

@Entity
@Table(name = "lis_dm_tipo_pedido_licenca_comercial")
@Getter
@Setter
public class TipoPedidoLicencaComercial extends EntityDB {
    String nome;
}
