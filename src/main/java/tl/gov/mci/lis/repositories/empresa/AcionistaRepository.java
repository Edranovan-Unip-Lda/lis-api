package tl.gov.mci.lis.repositories.empresa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.empresa.Acionista;

@JaversSpringDataAuditable
public interface AcionistaRepository extends JpaRepository<Acionista, Long> {
}