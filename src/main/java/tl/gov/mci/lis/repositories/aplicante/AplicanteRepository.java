package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.dtos.aplicante.AplicanteDto;
import tl.gov.mci.lis.models.aplicante.Aplicante;

import java.util.Optional;

@JaversSpringDataAuditable
public interface AplicanteRepository extends JpaRepository<Aplicante, Long> {

    @Query("""
                SELECT new tl.gov.mci.lis.dtos.aplicante.AplicanteDto(
                    a.id, a.isDeleted, a.createdAt, a.updatedAt, a.createdBy, a.updatedBy,
                    a.tipo, a.categoria, a.numero, a.estado,
                    p.status, f.status,
                    e.id, p.id
                )
                FROM Aplicante a
                JOIN a.empresa e
                JOIN a.pedido p
                JOIN p.fatura f
                WHERE a.id = :id
            """)
    Optional<AplicanteDto> getFromId(Long id);

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

}