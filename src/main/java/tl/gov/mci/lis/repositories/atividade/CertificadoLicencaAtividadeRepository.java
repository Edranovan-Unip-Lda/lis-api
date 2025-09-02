package tl.gov.mci.lis.repositories.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.atividade.CertificadoLicencaAtividade;

import java.util.Optional;

@JaversSpringDataAuditable
public interface CertificadoLicencaAtividadeRepository extends JpaRepository<CertificadoLicencaAtividade, Long> {

    Optional<CertificadoLicencaAtividade> findByPedidoLicencaAtividade_Id(Long id);
}