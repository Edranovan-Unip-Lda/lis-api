package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.dadosmestre.SociedadeComercial;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "sociedadeComercial", path = "sociedade-comercial")
public interface SociedadeComercialRepository extends JpaRepository<SociedadeComercial, Long> {
}