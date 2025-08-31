package tl.gov.mci.lis.repositories.endereco;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.endereco.PostoAdministrativo;

import java.util.List;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "postos", path = "postos")
public interface PostoAdministrativoRepository extends JpaRepository<PostoAdministrativo, Long> {
    List<PostoAdministrativo> findByNomeContainingIgnoreCase(String nome);
}