package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.dadosmestre.TipoPedidoVistoriaIndustrial;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "tipoPedidoVistoriaIndustrial", path = "tipo-pedido-vistoria-industrial")
public interface TipoPedidoVistoriaIndustrialRepository extends JpaRepository<TipoPedidoVistoriaIndustrial, Long> {
}