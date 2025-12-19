package tl.gov.mci.lis.repositories.empresa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.empresa.Empresa;
import tl.gov.mci.lis.repositories.projection.CategoryCountProjection;
import tl.gov.mci.lis.repositories.projection.MunicipioCountProjection;

import java.util.List;
import java.util.Optional;

@JaversSpringDataAuditable
public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {
    Optional<Empresa> findByUtilizador_Id(Long id);

    @Query("select new Empresa(e.id, e.nome, e.nif) from Empresa e where e.id = ?1")
    Empresa getFromId(Long id);

    @EntityGraph(attributePaths = {"acionistas", "documentos", "sede", "sociedadeComercial"})
    Optional<Empresa> findByUtilizador_Username(String username);

    long countByUtilizador_Status(String status);

    long countByIsDeletedFalse();

    // Count empresas by municipio for dashboard map
    @Query("""
            SELECT e.sede.aldeia.suco.postoAdministrativo.municipio.nome AS municipio,
                   COUNT(e) AS total
            FROM Empresa e
            WHERE e.isDeleted = false
            GROUP BY e.sede.aldeia.suco.postoAdministrativo.municipio.nome
            """)
    List<MunicipioCountProjection> countByMunicipio();

    // Count empresas by SociedadeComercial type
    @Query("""
            SELECT e.sociedadeComercial.nome AS category,
                   COUNT(e) AS total
            FROM Empresa e
            WHERE e.isDeleted = false AND e.sociedadeComercial IS NOT NULL
            GROUP BY e.sociedadeComercial.nome
            """)
    List<CategoryCountProjection> countBySociedadeComercial();

    // Count empresas by TipoEmpresa (MICROEMPRESA, PEQUENA, MÉDIA, GRANDE)
    @Query("""
            SELECT CAST(e.tipoEmpresa AS string) AS category,
                   COUNT(e) AS total
            FROM Empresa e
            WHERE e.isDeleted = false AND e.tipoEmpresa IS NOT NULL
            GROUP BY e.tipoEmpresa
            """)
    List<CategoryCountProjection> countByTipoEmpresa();
}