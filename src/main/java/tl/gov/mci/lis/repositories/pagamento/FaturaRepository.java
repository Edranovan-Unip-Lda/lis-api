package tl.gov.mci.lis.repositories.pagamento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.models.pagamento.Fatura;

import java.util.Optional;

@JaversSpringDataAuditable
public interface FaturaRepository extends JpaRepository<Fatura, Long> {

    @EntityGraph(attributePaths = {
            "pedidoInscricaoCadastro",
            "pedidoLicencaAtividade",
            "recibo",
            "taxas"
    })
    Optional<Fatura> findByIdAndPedidoInscricaoCadastro_Id(Long id, Long pedidoInscricaoCadastroId);

    Optional<Fatura> findByIdAndPedidoInscricaoCadastro_IdAndPedidoInscricaoCadastro_Aplicante_Id(Long id, Long pedidoId, Long aplicanteId);

    @EntityGraph(attributePaths = {
            "pedidoInscricaoCadastro",
            "pedidoLicencaAtividade",
            "recibo",
            "taxas"
    })
    Optional<Fatura> findByIdAndPedidoLicencaAtividade_Id(Long id, Long pedidoLicencaAtividadeId);

    @EntityGraph(attributePaths = {
            "pedidoInscricaoCadastro",
            "pedidoLicencaAtividade",
            "recibo",
            "taxas"
    })
    Optional<Fatura> findByIdAndPedidoVistoria_Id(Long id, Long pedidoLicencaAtividadeId);

    @Query("""
                select distinct f
                from Fatura f
                left join fetch f.pedidoInscricaoCadastro pic
                left join fetch f.pedidoLicencaAtividade pla
                left join fetch f.recibo r
                left join fetch f.taxas t
                where f.id = :id
            """)
    Optional<Fatura> findDetailById(@Param("id") Long id);
}