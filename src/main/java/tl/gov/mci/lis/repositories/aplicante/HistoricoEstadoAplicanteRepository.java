package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.dtos.aplicante.HistoricoEstadoAplicanteDto;
import tl.gov.mci.lis.models.aplicante.HistoricoEstadoAplicante;

import java.util.List;

@JaversSpringDataAuditable
public interface HistoricoEstadoAplicanteRepository extends JpaRepository<HistoricoEstadoAplicante, Long> {

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.aplicante.HistoricoEstadoAplicanteDto(h.id,h.status,h.descricao,h.alteradoPor,h.dataAlteracao)
            FROM HistoricoEstadoAplicante h
            WHERE h.aplicante.id = :aplicanteId
            ORDER BY h.id DESC
            """)
    List<HistoricoEstadoAplicanteDto> findAllDtoByAplicante_Id(Long aplicanteId);

    List<HistoricoEstadoAplicante> findAllByAplicante_Id(Long aplicanteId);
}