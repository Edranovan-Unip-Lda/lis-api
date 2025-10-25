package tl.gov.mci.lis.repositories.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.enums.PedidoStatus;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;

import java.util.Optional;

@JaversSpringDataAuditable
public interface PedidoLicencaAtividadeRepository extends JpaRepository<PedidoLicencaAtividade, Long> {

    @EntityGraph(attributePaths = {
            "empresaSede",
            "representante", "representante.morada",
            "gerente", "gerente.morada",
            "fatura", "certificadoLicencaAtividade"
    })
    Optional<PedidoLicencaAtividade> findByAplicante_id(Long id);

    @EntityGraph(attributePaths = {
            "empresaSede",
            "representante", "representante.morada",
            "gerente", "gerente.morada"
    })
    Optional<PedidoLicencaAtividade> findByIdAndAplicante_id(Long pedidoId, Long aplicanteId);

    @EntityGraph(attributePaths = {
            "empresaSede",
            "representante", "representante.morada",
            "gerente", "gerente.morada",
            "fatura", "certificadoLicencaAtividade"
    })
    Optional<PedidoLicencaAtividade> findByAplicante_IdAndStatus(Long aplicanteId, PedidoStatus status);

    @EntityGraph(attributePaths = {
            "empresaSede",
            "representante", "representante.morada",
            "gerente", "gerente.morada",
            "fatura"
    })
    Optional<PedidoLicencaAtividade> findDetailById(Long pedidoId);
}