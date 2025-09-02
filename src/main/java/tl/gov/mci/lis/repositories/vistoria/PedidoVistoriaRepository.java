package tl.gov.mci.lis.repositories.vistoria;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.vistoria.PedidoVistoria;

import java.util.Optional;
import java.util.Set;

@JaversSpringDataAuditable
public interface PedidoVistoriaRepository extends JpaRepository<PedidoVistoria, Long> {

    @EntityGraph(attributePaths = {
            "fatura"
    })
    Optional<PedidoVistoria> findByIdAndPedidoLicencaAtividade_Id(Long pedidoId, Long pedidoLicencaAtividadeId);


    @EntityGraph(attributePaths = {
            "fatura"
    })
    Optional<PedidoVistoria> findDetailById(Long pedidoId);

    @EntityGraph(attributePaths = {
            "fatura"
    })
    Set<PedidoVistoria> findByPedidoLicencaAtividade_Id(Long pedidoLicencaAtividadeId);
}