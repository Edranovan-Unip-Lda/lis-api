package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.aplicante.Aplicante;

@JaversSpringDataAuditable
public interface AplicanteRepository extends JpaRepository<Aplicante, Long> {

    @Query("SELECT new Aplicante (a.id, a.empresa.id, a.empresa.nome, a.empresa.nif, a.estado, a.numero, a.categoria, a.tipo, a.createdAt, a.updatedAt) FROM Aplicante a")
    Page<Aplicante> getPageBy(Pageable pageable);
}