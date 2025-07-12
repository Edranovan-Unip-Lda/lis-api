package tl.gov.mci.lis.repositories.pagamento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.pagamento.Taxa;

@JaversSpringDataAuditable
public interface TaxaRepository extends JpaRepository<Taxa, Long> {
}