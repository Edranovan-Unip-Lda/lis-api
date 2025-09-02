package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.models.aplicante.Aplicante;

import java.util.Optional;

@JaversSpringDataAuditable
public interface AplicanteRepository extends JpaRepository<Aplicante, Long> {

    @Query("""
                SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(
                    a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy,
                    a.tipo, a.categoria, a.numero, a.estado,
                    p.status, f.status
                )
                FROM Aplicante a
                LEFT JOIN PedidoInscricaoCadastro p ON p.aplicante.id = a.id
                LEFT JOIN Fatura f ON f.pedidoInscricaoCadastro.id = p.id
                WHERE a.estado = :estado
            """)
    Page<AplicanteDto> getPageApprovedAplicante(AplicanteStatus estado, Pageable pageable);

    @Query("""
                SELECT a
                FROM Aplicante a
                JOIN a.empresa e
                JOIN a.pedidoInscricaoCadastro p
                JOIN p.fatura f
                WHERE a.id = ?1
            """)
    Optional<Aplicante> getFromId(Long id);

    @Query("""
                SELECT a
                FROM Aplicante a
                JOIN a.empresa e
                WHERE a.id = :id AND a.direcaoAtribuida.id = :direcaoId
            """)
    Optional<Aplicante> getFromIdAndDirecaoId(Long id, Long direcaoId);

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy, a.tipo, a.categoria, a.numero, a.estado, p.status, f.status) FROM Aplicante a
            LEFT JOIN PedidoInscricaoCadastro p ON p.aplicante.id = a.id
            LEFT JOIN Fatura f ON f.pedidoInscricaoCadastro.id = p.id
            WHERE a.id = ?1 AND a.empresa.id = ?2
            """)
    Optional<AplicanteDto> getFromIdAndEmpresaId(Long id, Long empresaId);

    @Query("""
                SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(
                    a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy,
                    a.tipo, a.categoria, a.numero, a.estado,
                    p.status, f.status
                )
                FROM Aplicante a
                LEFT JOIN PedidoInscricaoCadastro p ON p.aplicante.id = a.id
                LEFT JOIN Fatura f ON f.pedidoInscricaoCadastro.id = p.id
                WHERE a.empresa.id = :empresaId
            """)
    Page<AplicanteDto> getPageByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("""
                SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(
                    a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy,
                    a.tipo, a.categoria, a.numero, a.estado,
                    p.status, f.status
                )
                FROM Aplicante a
                LEFT JOIN PedidoInscricaoCadastro p ON p.aplicante.id = a.id
                LEFT JOIN Fatura f ON f.pedidoInscricaoCadastro.id = p.id
                WHERE a.direcaoAtribuida.id = :direcaoId
            """)
    Page<AplicanteDto> getPageByDirecaoId(@Param("direcaoId") Long direcaoId, Pageable pageable);

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy, a.tipo, a.categoria, a.numero, a.estado, p.status, f.status) FROM Aplicante a
            LEFT JOIN PedidoInscricaoCadastro p ON p.aplicante.id = a.id
            LEFT JOIN Fatura f ON f.pedidoInscricaoCadastro.id = p.id
            WHERE a.id = ?1 AND a.direcaoAtribuida.id = ?2
            """)
    Optional<AplicanteDto> findByIdAndDirecao_Id(Long id, Long direcaoId);

    Optional<Aplicante> findByIdAndEmpresa_id(Long id, Long empresaId);

    @EntityGraph(attributePaths = {"empresa", "empresa.sede"})
    @Query("SELECT a FROM Aplicante a WHERE a.id = :id")
    Optional<Aplicante> findByIdWithEmpresaAndEmpresa_Sede(Long id);

    // 1 query, brings: pedido -> (empresaSede, classeAtividade), empresa, certificado, historicoStatus
    @Query("""
                select distinct a
                from Aplicante a
                left join fetch a.pedidoInscricaoCadastro pi
                left join fetch a.pedidoLicencaAtividade pl
                left join fetch pi.empresaSede
                left join fetch pi.classeAtividade
                left join fetch pl.empresaSede
                left join fetch pl.tipoAtividade
                left join fetch a.empresa
                left join fetch a.historicoStatus hs
                where a.id = :id
            """)
    Optional<Aplicante> findByIdWithAllForApproval(@Param("id") Long id);
}