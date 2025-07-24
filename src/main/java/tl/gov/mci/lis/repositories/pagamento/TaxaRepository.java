package tl.gov.mci.lis.repositories.pagamento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.pagamento.Taxa;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "taxas", path = "taxas")
public interface TaxaRepository extends JpaRepository<Taxa, Long> {
}