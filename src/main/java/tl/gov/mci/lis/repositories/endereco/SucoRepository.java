package tl.gov.mci.lis.repositories.endereco;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.endereco.Suco;

import java.util.List;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "sucos", path = "sucos")
public interface SucoRepository extends JpaRepository<Suco, Long> {
    List<Suco> findByNomeContainingIgnoreCase(String nome);
}