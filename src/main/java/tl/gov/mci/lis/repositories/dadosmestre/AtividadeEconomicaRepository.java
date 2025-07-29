package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import tl.gov.mci.lis.enums.TipoAtividadeEconomica;
import tl.gov.mci.lis.models.dadosmestre.AtividadeEconomica;

import java.util.List;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "atividadeEconomica", path = "atividade-economica")
public interface AtividadeEconomicaRepository extends JpaRepository<AtividadeEconomica, Long>, JpaSpecificationExecutor<AtividadeEconomica> {
    @Override
    @RestResource(exported = false)
    void deleteById(Long id);

    @Override
    List<AtividadeEconomica> findAll(); // exposed at /data/atividade-economica/search/findAll

    List<AtividadeEconomica> findByTipoAtividadeEconomica(@Param("tipoAtividadeEconomica") TipoAtividadeEconomica tipoAtividadeEconomica);
}