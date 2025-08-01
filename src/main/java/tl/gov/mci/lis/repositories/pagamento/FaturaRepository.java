package tl.gov.mci.lis.repositories.pagamento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.pagamento.Fatura;

import java.util.Optional;

@JaversSpringDataAuditable
public interface FaturaRepository extends JpaRepository<Fatura, Long> {

    Optional<Fatura> findByIdAndPedidoInscricaoCadastro_Id(Long id, Long pedidoInscricaoCadastroId);

    Optional<Fatura> findByIdAndPedidoInscricaoCadastro_IdAndPedidoInscricaoCadastro_Aplicante_Id(Long id, Long pedidoId, Long aplicanteId);
}