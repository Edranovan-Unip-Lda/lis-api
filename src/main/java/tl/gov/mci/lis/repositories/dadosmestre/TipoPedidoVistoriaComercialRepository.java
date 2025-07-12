package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.dadosmestre.TipoPedidoVistoriaComercial;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "tipoPedidoVistoriaComercial", path = "tipo-pedido-vistoria-comercial")
public interface TipoPedidoVistoriaComercialRepository extends JpaRepository<TipoPedidoVistoriaComercial, Long> {
}