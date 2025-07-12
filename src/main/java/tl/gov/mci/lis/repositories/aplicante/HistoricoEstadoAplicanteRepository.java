package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;

@JaversSpringDataAuditable
public interface HistoricoEstadoAplicanteRepository extends JpaRepository<HistoricoEstadoAplicante, Long> {
}