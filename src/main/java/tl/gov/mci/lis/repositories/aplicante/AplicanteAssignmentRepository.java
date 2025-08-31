package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.aplicante.AplicanteAssignment;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface AplicanteAssignmentRepository extends JpaRepository<AplicanteAssignment, Long> {
    List<AplicanteAssignment> findByAplicanteIdAndActiveTrueOrderByAssignedAtDesc(Long aplicanteId);

    @Query("""
                SELECT a.aplicante
                FROM AplicanteAssignment a
                WHERE a.assignee.id = :staffId
                  AND a.active = true
            """)
    Page<Aplicante> findActiveAplicantesByStaffId(@Param("staffId") Long staffId, Pageable pageable);

    boolean existsByAplicanteIdAndAssigneeIdAndActiveTrue(Long aplicanteId, Long staffId);

    Optional<AplicanteAssignment> findByAplicante_IdAndActiveTrue(Long aplicanteId);
}