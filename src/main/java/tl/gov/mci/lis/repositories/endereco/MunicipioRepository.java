package tl.gov.mci.lis.repositories.endereco;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import tl.gov.mci.lis.models.endereco.Municipio;

import java.util.List;

@JaversSpringDataAuditable
@RepositoryRestResource(collectionResourceRel = "municipios", path = "municipios")
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {
    @Query("SELECT new Municipio(m.id, m.nome) FROM Municipio m WHERE m.isDeleted = false")
    List<Municipio> getAll();

    @Query("SELECT new Municipio(m.id, m.nome) FROM Municipio m WHERE m.id = ?1")
    Municipio getFromId(Long id);
}