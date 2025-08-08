package tl.gov.mci.lis.repositories.cadastro;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.cadastro.CertificadoInscricaoCadastro;

import java.util.Optional;

@JaversSpringDataAuditable
public interface CertificadoInscricaoCadastroRepository extends JpaRepository<CertificadoInscricaoCadastro, Long> {

    Optional<CertificadoInscricaoCadastro> findByAplicante_Id(Long id);
}