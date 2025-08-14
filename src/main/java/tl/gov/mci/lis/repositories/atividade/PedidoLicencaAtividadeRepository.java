package tl.gov.mci.lis.repositories.atividade;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.repository.Repository;
import tl.gov.mci.lis.models.atividade.PedidoLicencaAtividade;

@JaversSpringDataAuditable
public interface PedidoLicencaAtividadeRepository extends Repository<PedidoLicencaAtividade, Long> {
}