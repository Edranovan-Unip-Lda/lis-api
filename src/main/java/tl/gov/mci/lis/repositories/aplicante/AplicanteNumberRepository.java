package tl.gov.mci.lis.repositories.aplicante;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.models.aplicante.AplicanteNumber;

@JaversSpringDataAuditable
public interface AplicanteNumberRepository extends JpaRepository<AplicanteNumber, Long> {
    @Query("SELECT COUNT(a) FROM AplicanteNumber a WHERE a.categoriaCode = :categoriaCode AND a.month = :month AND a.year = :year")
    int countAllByCategoriaCodeAndMonthAndYear(@Param("categoriaCode") String categoriaCode,
                                               @Param("month") int month,
                                               @Param("year") int year);
    int countByCategoriaCodeAndMonthAndYear(String categoriaCode, int month, int year);
}