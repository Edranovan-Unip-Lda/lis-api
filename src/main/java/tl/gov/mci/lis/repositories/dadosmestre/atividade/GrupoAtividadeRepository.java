package tl.gov.mci.lis.repositories.dadosmestre.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.dadosmestre.atividade.GrupoAtividade;

import java.util.List;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "grupoAtividade", path = "grupo-atividades")
public interface GrupoAtividadeRepository extends JpaRepository<GrupoAtividade, Long> {

    @Override
    List<GrupoAtividade> findAll();

    List<GrupoAtividade> findByTipo(@Param("tipo") Categoria tipo);
}