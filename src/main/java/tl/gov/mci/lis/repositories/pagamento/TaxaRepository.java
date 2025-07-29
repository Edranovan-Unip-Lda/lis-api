package tl.gov.mci.lis.repositories.pagamento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.enums.AplicanteType;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.pagamento.Taxa;

import java.util.List;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "taxas", path = "taxas")
public interface TaxaRepository extends JpaRepository<Taxa, Long> {

    List<Taxa> findByCategoriaAndTipo(@Param("categoria") Categoria categoria, @Param("tipo") AplicanteType tipo);
}