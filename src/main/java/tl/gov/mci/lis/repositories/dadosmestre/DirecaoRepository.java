package tl.gov.mci.lis.repositories.dadosmestre;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.enums.Categoria;
import tl.gov.mci.lis.models.dadosmestre.Direcao;

import java.util.Optional;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "direcoes", path = "direcoes")
public interface DirecaoRepository extends JpaRepository<Direcao, Long> {

    Optional<Direcao> findByNome(Categoria nome);

}