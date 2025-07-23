package tl.gov.mci.lis.repositories.documento;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.documento.Documento;

@JaversSpringDataAuditable
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
}