package tl.gov.mci.lis.repositories.dadosmestre.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.dadosmestre.atividade.ClasseAtividade;

import java.util.List;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "classeAtividade", path = "classe-atividades")
public interface ClasseAtividadeRepository extends JpaRepository<ClasseAtividade, Long> {
    List<ClasseAtividade> findByTipo(@Param("tipo") Categoria tipo);
}