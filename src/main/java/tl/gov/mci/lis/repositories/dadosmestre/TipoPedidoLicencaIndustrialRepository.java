package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.dadosmestre.TipoPedidoLicencaIndustrial;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "tipoPedidoLicencaIndustrial", path = "tipo-pedido-licenca-industrial")
public interface TipoPedidoLicencaIndustrialRepository extends JpaRepository<TipoPedidoLicencaIndustrial, Long> {
}