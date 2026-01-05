package tl.gov.mci.lis.repositories.atividade;

import io.lettuce.core.dynamic.annotation.Param;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.dtos.atividade.CertificadoLicencaAtividadeListDto;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;
import tl.gov.mci.lis.repositories.projection.MonthCountProjection;
import tl.gov.mci.lis.repositories.projection.MonthTypeCountProjection;
import tl.gov.mci.lis.repositories.projection.MunicipioCountProjection;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface CertificadoLicencaAtividadeRepository extends JpaRepository<CertificadoLicencaAtividade, Long>, JpaSpecificationExecutor<CertificadoLicencaAtividade> {

    Optional<CertificadoLicencaAtividade> findByPedidoLicencaAtividade_Id(Long id);

    @Query("""
            SELECT cert FROM CertificadoLicencaAtividade cert
                        WHERE cert.id = :id
                        AND cert.pedidoLicencaAtividade.aplicante.categoria = :categoria
                        AND cert.pedidoLicencaAtividade.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Optional<CertificadoLicencaAtividade> findByIdAndAplicanteIdAndCategoria(Long id, Categoria categoria);

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.atividade.CertificadoLicencaAtividadeListDto(
                cert.id, cert.isDeleted, cert.createdAt, cert.updatedAt, cert.createdBy, cert.updatedBy,
                cert.sociedadeComercial, cert.numeroRegistoComercial, cert.nif, cert.nivelRisco,
                cert.atividade, cert.atividadeCodigo, cert.dataValidade, cert.dataEmissao, cert.nomeDiretorGeral,
                cert.sede.local, 
                cert.sede.aldeia.nome, 
                cert.sede.aldeia.suco.nome,
                cert.sede.aldeia.suco.postoAdministrativo.nome,
                cert.sede.aldeia.suco.postoAdministrativo.municipio.nome,
                cert.pedidoLicencaAtividade.aplicante.numero
            )
            FROM CertificadoLicencaAtividade cert
            LEFT JOIN cert.sede
            LEFT JOIN cert.sede.aldeia
            LEFT JOIN cert.sede.aldeia.suco
            LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo
            LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo.municipio
            WHERE cert.pedidoLicencaAtividade.aplicante.categoria = :categoria
            AND cert.pedidoLicencaAtividade.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Page<CertificadoLicencaAtividadeListDto> findApprovedByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.atividade.CertificadoLicencaAtividadeListDto(
                cert.id, cert.isDeleted, cert.createdAt, cert.updatedAt, cert.createdBy, cert.updatedBy,
                cert.sociedadeComercial, cert.numeroRegistoComercial, cert.nif, cert.nivelRisco,
                cert.atividade, cert.atividadeCodigo, cert.dataValidade, cert.dataEmissao, cert.nomeDiretorGeral,
                cert.sede.local, 
                cert.sede.aldeia.nome, 
                cert.sede.aldeia.suco.nome,
                cert.sede.aldeia.suco.postoAdministrativo.nome,
                cert.sede.aldeia.suco.postoAdministrativo.municipio.nome,
                cert.pedidoLicencaAtividade.aplicante.numero
            )
            FROM CertificadoLicencaAtividade cert
            LEFT JOIN cert.sede
            LEFT JOIN cert.sede.aldeia
            LEFT JOIN cert.sede.aldeia.suco
            LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo
            LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo.municipio
            WHERE cert.pedidoLicencaAtividade.aplicante.empresa.id = :empresaId
            AND cert.pedidoLicencaAtividade.aplicante.categoria = :categoria
            AND cert.pedidoLicencaAtividade.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Page<CertificadoLicencaAtividadeListDto> findApprovedByEmpresaIdAndCategoria(@Param("empresaId") Long empresaId, @Param("categoria") Categoria categoria, Pageable pageable);

    //Repositories for Dashboard Service

    @Query("""
    SELECT COUNT(c)
    FROM CertificadoLicencaAtividade c
    WHERE c.dataValidade IS NULL OR c.dataValidade > :today
    """)
    long countActiveByDataValidade(@Param("today") String today);

    @Query("""
    SELECT COUNT(c)
    FROM CertificadoLicencaAtividade c
    WHERE c.dataValidade IS NOT NULL AND c.dataValidade <= :today
    """)
    long countExpiredByDataValidade(@Param("today") String today);


    // Licenses per month (by tipoLicenca) for a given year
    @Query("""
        SELECT CAST(SUBSTRING(c.dataEmissao, 6, 2) AS int) AS month,
               c.pedidoLicencaAtividade.aplicante.categoria AS tipoLicenca,
               COUNT(c) AS total
        FROM CertificadoLicencaAtividade c
        WHERE SUBSTRING(c.dataEmissao, 1, 4) = CAST(:year AS string)
        GROUP BY SUBSTRING(c.dataEmissao, 6, 2), tipoLicenca
        ORDER BY SUBSTRING(c.dataEmissao, 6, 2)
        """)
    List<MonthTypeCountProjection> countByMonthAndTipoLicenca(@Param("year") int year);

    // Distribution by municipio for active licenses
    @Query("""
            SELECT c.sede.aldeia.suco.postoAdministrativo.municipio.nome AS municipio,
                   COUNT(c)     AS total
            FROM CertificadoLicencaAtividade c
            WHERE
              c.dataValidade IS NULL OR c.dataValidade > :today
            GROUP BY c.sede.aldeia.suco.postoAdministrativo.municipio.nome
            """)
    List<MunicipioCountProjection> countActiveByMunicipio(@Param("today") String today);
}