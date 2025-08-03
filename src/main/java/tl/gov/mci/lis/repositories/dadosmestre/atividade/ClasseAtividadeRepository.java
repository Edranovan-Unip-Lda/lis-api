package tl.gov.mci.lis.repositories.dadosmestre.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "classeAtividade", path = "classe-atividades")
public interface ClasseAtividadeRepository extends JpaRepository<ClasseAtividade, Long> {
}