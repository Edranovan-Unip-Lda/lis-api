package tl.gov.mci.lis.repositories.empresa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.empresa.Empresa;

import java.util.Optional;

@JaversSpringDataAuditable
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByUtilizador_Id(Long id);
}