package tl.gov.mci.lis.repositories.empresa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.empresa.Empresa;

import java.util.Optional;

@JaversSpringDataAuditable
public interface EmpresaRepository extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {
    Optional<Empresa> findByUtilizador_Id(Long id);

    @Query("select new Empresa(e.id, e.nome, e.nif) from Empresa e where e.id = ?1")
    Empresa getFromId(Long id);

    @EntityGraph(attributePaths = {"acionistas", "documentos", "sede", "sociedadeComercial"})
    Optional<Empresa> findByUtilizador_Username(String username);
}