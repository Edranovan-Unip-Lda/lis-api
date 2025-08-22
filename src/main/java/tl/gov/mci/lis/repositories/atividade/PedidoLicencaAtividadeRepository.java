package tl.gov.mci.lis.repositories.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.Repository;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;

import java.util.Optional;

@JaversSpringDataAuditable
public interface PedidoLicencaAtividadeRepository extends Repository<PedidoLicencaAtividade, Long> {

    @EntityGraph(attributePaths = {
            "empresaSede",
            "representante", "representante.morada",
            "gerente", "gerente.morada"
    })
    Optional<PedidoLicencaAtividade> findByAplicante_id(Long id);

    @EntityGraph(attributePaths = {
            "empresaSede",
            "representante", "representante.morada",
            "gerente", "gerente.morada"
    })
    Optional<PedidoLicencaAtividade> findByIdAndAplicante_id(Long pedidoId, Long aplicanteId);
}