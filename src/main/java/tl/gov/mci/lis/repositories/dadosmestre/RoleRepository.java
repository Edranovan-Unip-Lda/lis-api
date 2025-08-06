package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.dadosmestre.Role;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "roles", path = "roles")
public interface RoleRepository extends JpaRepository<Role, Long> {
}