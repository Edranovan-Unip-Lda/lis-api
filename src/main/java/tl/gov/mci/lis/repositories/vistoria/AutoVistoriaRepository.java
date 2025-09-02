package tl.gov.mci.lis.repositories.vistoria;

import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.vistoria.AutoVistoria;

public interface AutoVistoriaRepository extends JpaRepository<AutoVistoria, Long> {
}