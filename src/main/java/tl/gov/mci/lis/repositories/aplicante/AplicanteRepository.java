package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.models.aplicante.Aplicante;

import java.util.Optional;

@JaversSpringDataAuditable
public interface AplicanteRepository extends JpaRepository<Aplicante, Long>, JpaSpecificationExecutor<Aplicante> {

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(
                a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy,
                a.tipo, a.categoria, a.numero, a.estado, a.empresa.id, a.empresa.nome
            )
            FROM Aplicante a
            WHERE a.estado = :estado
            """)
    Page<AplicanteDto> getPageApprovedAplicante(AplicanteStatus estado, Pageable pageable);

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy, a.tipo, a.categoria, a.numero, a.estado) FROM Aplicante a
            WHERE a.id = ?1 AND a.empresa.id = ?2
            """)
    Optional<AplicanteDto> getFromIdAndEmpresaId(Long id, Long empresaId);

    @Query("""
                SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(
                    a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy,
                    a.tipo, a.categoria, a.numero, a.estado
                )
                FROM Aplicante a
                WHERE a.empresa.id = :empresaId
            """)
    Page<AplicanteDto> getPageByEmpresaId(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("""
                SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(
                    a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy,
                    a.tipo, a.categoria, a.numero, a.estado
                )
                FROM Aplicante a
                WHERE a.direcaoAtribuida.id = :direcaoId
            """)
    Page<AplicanteDto> getPageByDirecaoId(@Param("direcaoId") Long direcaoId, Pageable pageable);

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
                left join fetch pl.classeAtividade
                left join fetch a.empresa
                left join fetch a.historicoStatus hs
                where a.id = :id
            """)
    Optional<Aplicante> findByIdWithAllForApproval(@Param("id") Long id);

    // If status is a String:
    long countByEstado(AplicanteStatus estado);

    // If status is an enum, switch to:
    // long countByStatus(AplicanteStatus status);
    // and update service accordingly.

    @Query("select count(a) from Aplicante a where a.estado = 'EM_CURSO'")
    long countInProgressFallback();
}