package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.dadosmestre.TipoPedidoLicencaComercial;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "tipoPedidoLicencaComercial", path = "tipo-pedido-licenca-comercial")
public interface TipoPedidoLicencaComercialRepository extends JpaRepository<TipoPedidoLicencaComercial, Long> {
}