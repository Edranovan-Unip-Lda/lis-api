package tl.gov.mci.lis.repositories.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;
import tl.gov.mci.lis.models.user.UserNotificacao;

import java.util.List;

public interface UserNotificacaoRepository extends JpaRepository<UserNotificacao, Long> {
    List<UserNotificacao> findByDestinatario_IdOrderByIdDesc(Long id);
}