package tl.gov.mci.lis.repositories.notificacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tl.gov.mci.lis.dtos.notificacao.NotificacaoDto;
import tl.gov.mci.lis.models.user.UserNotificacao;

import java.util.List;

public interface UserNotificacaoRepository extends JpaRepository<UserNotificacao, Long> {
    List<UserNotificacao> findByDestinatario_IdOrderByIdDesc(Long id);

    @Query("""
        SELECT new tl.gov.mci.lis.dtos.notificacao.NotificacaoDto(
            un.id,
            n.id,
            un.title,
            un.description,
            un.visto,
            un.vistoEm,
            un.createdAt,
            a.id,
            a.numero,
            n.aplicanteStatus,
            a.tipo,
            a.categoria,
            e.id,
            e.nome,
            e.nif
        )
        FROM UserNotificacao un
        JOIN un.notificacao n
        JOIN n.aplicante a
        JOIN a.empresa e
        WHERE un.destinatario.id = :userId
        ORDER BY un.createdAt DESC
    """)
    List<NotificacaoDto> findNotificationsByUserId(@Param("userId") Long userId);

    @Query("""
        SELECT new tl.gov.mci.lis.dtos.notificacao.NotificacaoDto(
            un.id,
            n.id,
            un.title,
            un.description,
            un.visto,
            un.vistoEm,
            un.createdAt,
            a.id,
            a.numero,
            n.aplicanteStatus,
            a.tipo,
            a.categoria,
            e.id,
            e.nome,
            e.nif
        )
        FROM UserNotificacao un
        JOIN un.notificacao n
        JOIN n.aplicante a
        JOIN a.empresa e
        WHERE un.visto = false AND un.destinatario.id = :userId
        ORDER BY un.createdAt DESC
    """)
    List<NotificacaoDto> findByVistoFalseAndDestinatario_Id(Long userId);

    @Query("""
        SELECT new tl.gov.mci.lis.dtos.notificacao.NotificacaoDto(
            un.id,
            n.id,
            un.title,
            un.description,
            un.visto,
            un.vistoEm,
            un.createdAt,
            a.id,
            a.numero,
            n.aplicanteStatus,
            a.tipo,
            a.categoria,
            e.id,
            e.nome,
            e.nif
        )
        FROM UserNotificacao un
        JOIN un.notificacao n
        JOIN n.aplicante a
        JOIN a.empresa e
        WHERE un.destinatario.id = :userId
    """)
    Page<NotificacaoDto> findNotificationsByUserIdPaginated(@Param("userId") Long userId, Pageable pageable);
}