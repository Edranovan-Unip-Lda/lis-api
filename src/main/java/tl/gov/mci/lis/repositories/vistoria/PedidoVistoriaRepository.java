package tl.gov.mci.lis.repositories.vistoria;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;

import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
public interface PedidoVistoriaRepository extends JpaRepository<PedidoVistoria, Long> {

    @EntityGraph(attributePaths = {
            "fatura"
    })
    Optional<PedidoVistoria> findByIdAndAplicante_id(Long pedidoId, Long aplicanteId);


    @EntityGraph(attributePaths = {
            "fatura"
    })
    Optional<PedidoVistoria> findDetailById(Long pedidoId);

    @EntityGraph(attributePaths = {
            "fatura"
    })
    Set<PedidoVistoria> findByAplicante_id(Long aplicanteId);
}