package tl.gov.mci.lis.repositories.cadastro;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroListDto;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;
import tl.gov.mci.lis.repositories.projection.MonthTypeCountProjection;
import tl.gov.mci.lis.repositories.projection.MunicipioCountProjection;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface CertificadoInscricaoCadastroRepository extends JpaRepository<CertificadoInscricaoCadastro, Long>, JpaSpecificationExecutor<CertificadoInscricaoCadastro> {

    Optional<CertificadoInscricaoCadastro> findByPedidoInscricaoCadastro_Id(Long id);

    @Query("""
            SELECT cert FROM CertificadoInscricaoCadastro cert
                        WHERE cert.id = :id
                        AND cert.pedidoInscricaoCadastro.aplicante.categoria = :categoria
                        AND cert.pedidoInscricaoCadastro.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Optional<CertificadoInscricaoCadastro> findByIdAndAplicanteIdAndCategoria(Long id, Categoria categoria);

    @Query("""
             SELECT new tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroListDto(
                cert.id, cert.isDeleted, cert.createdAt, cert.updatedAt, cert.createdBy, cert.updatedBy,
                 cert.sociedadeComercial, cert.numeroRegistoComercial, cert.atividade,
                 cert.dataValidade, cert.dataEmissao, cert.nomeDiretorGeral,
                 cert.sede.local,
                 cert.sede.aldeia.nome,
                 cert.sede.aldeia.suco.nome,
                 cert.sede.aldeia.suco.postoAdministrativo.nome,
                 cert.sede.aldeia.suco.postoAdministrativo.municipio.nome,
                 cert.pedidoInscricaoCadastro.aplicante.numero
             )
             FROM CertificadoInscricaoCadastro cert
             LEFT JOIN cert.sede
             LEFT JOIN cert.sede.aldeia
             LEFT JOIN cert.sede.aldeia.suco
             LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo
             LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo.municipio
             WHERE cert.pedidoInscricaoCadastro.aplicante.categoria = :categoria
             AND cert.pedidoInscricaoCadastro.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            """)
    Page<CertificadoInscricaoCadastroListDto> findApprovedByCategoria(@Param("categoria") Categoria categoria, Pageable pageable);

    @Query("""
            SELECT new tl.gov.mci.lis.dtos.cadastro.CertificadoInscricaoCadastroListDto(
                cert.id, cert.isDeleted, cert.createdAt, cert.updatedAt, cert.createdBy, cert.updatedBy,
                cert.sociedadeComercial, cert.numeroRegistoComercial, cert.atividade,
                cert.dataValidade, cert.dataEmissao, cert.nomeDiretorGeral,
                cert.sede.local,
                cert.sede.aldeia.nome,
                cert.sede.aldeia.suco.nome,
                cert.sede.aldeia.suco.postoAdministrativo.nome,
                cert.sede.aldeia.suco.postoAdministrativo.municipio.nome,
                cert.pedidoInscricaoCadastro.aplicante.numero
            )
            FROM CertificadoInscricaoCadastro cert
            LEFT JOIN cert.sede
            LEFT JOIN cert.sede.aldeia
            LEFT JOIN cert.sede.aldeia.suco
            LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo
            LEFT JOIN cert.sede.aldeia.suco.postoAdministrativo.municipio
            WHERE cert.pedidoInscricaoCadastro.aplicante.categoria = :categoria
            AND cert.pedidoInscricaoCadastro.aplicante.estado = tl.gov.mci.lis.enums.AplicanteStatus.APROVADO
            AND cert.pedidoInscricaoCadastro.aplicante.empresa.id = :empresaId
            """)
    Page<CertificadoInscricaoCadastroListDto> findApprovedByEmpresaIdAndCategoria(@Param("empresaId") Long empresaId, @Param("categoria") Categoria categoria, Pageable pageable);

    //Repositories for Dashboard Service

    @Query("""
    SELECT COUNT(c)
    FROM CertificadoInscricaoCadastro c
    WHERE c.dataValidade IS NULL OR c.dataValidade > :today
    """)
    long countActiveByDataValidade(@Param("today") String today);

    @Query("""
    SELECT COUNT(c)
    FROM CertificadoInscricaoCadastro c
    WHERE c.dataValidade IS NOT NULL AND c.dataValidade <= :today
    """)
    long countExpiredByDataValidade(@Param("today") String today);


    // Licenses per month (by tipoLicenca) for a given year
    @Query("""
        SELECT CAST(SUBSTRING(c.dataEmissao, 6, 2) AS int) AS month,
               c.pedidoInscricaoCadastro.aplicante.categoria AS tipoLicenca,
               COUNT(c) AS total
        FROM CertificadoInscricaoCadastro c
        WHERE SUBSTRING(c.dataEmissao, 1, 4) = CAST(:year AS string)
        GROUP BY SUBSTRING(c.dataEmissao, 6, 2), tipoLicenca
        ORDER BY SUBSTRING(c.dataEmissao, 6, 2)
        """)
    List<MonthTypeCountProjection> countByMonthAndTipoLicenca(@Param("year") int year);

    // Distribution by municipio for active licenses
    @Query("""
            SELECT c.sede.aldeia.suco.postoAdministrativo.municipio.nome AS municipio,
                   COUNT(c)     AS total
            FROM CertificadoInscricaoCadastro c
            WHERE
              c.dataValidade IS NULL OR c.dataValidade > :today
            GROUP BY c.sede.aldeia.suco.postoAdministrativo.municipio.nome
            """)
    List<MunicipioCountProjection> countActiveByMunicipio(@Param("today") String today);
}