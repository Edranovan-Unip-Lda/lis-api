package tl.gov.mci.lis.repositories.endereco;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.endereco.Aldeia;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "aldeias", path = "aldeias")
public interface AldeiaRepository extends JpaRepository<Aldeia, Long> {
}