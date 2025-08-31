package tl.gov.mci.lis.repositories.vistoria;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.vistoria.AutoVistoria;

import java.util.Optional;

public interface AutoVistoriaRepository extends JpaRepository<AutoVistoria, Long> {

    @EntityGraph(attributePaths = {
            "funcionario",
    })
    Optional<AutoVistoria> findByAplicante_id(Long aplicanteId);
}