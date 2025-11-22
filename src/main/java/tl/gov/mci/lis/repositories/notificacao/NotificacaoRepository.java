package tl.gov.mci.lis.repositories.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.notificacao.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
}