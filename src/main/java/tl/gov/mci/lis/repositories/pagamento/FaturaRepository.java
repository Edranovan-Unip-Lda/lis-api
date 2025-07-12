package tl.gov.mci.lis.repositories.pagamento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.pagamento.Fatura;

@JaversSpringDataAuditable
public interface FaturaRepository extends JpaRepository<Fatura, Long> {
}