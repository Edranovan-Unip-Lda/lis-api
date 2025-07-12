package tl.gov.mci.lis.repositories.endereco;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tl.gov.mci.lis.models.endereco.Endereco;

@JaversSpringDataAuditable
public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    @Query("" +
            "SELECT " +
            "new Endereco (en.id,en.local,en.aldeia.id,en.aldeia.nome,en.aldeia.suco.id,en.aldeia.suco.nome,en.aldeia.suco.postoAdministrativo.id,en.aldeia.suco.postoAdministrativo.nome,en.aldeia.suco.postoAdministrativo.municipio.id,en.aldeia.suco.postoAdministrativo.municipio.nome) " +
            "FROM Endereco en WHERE en.id = ?1")
    Endereco getFromId(Long id);
}