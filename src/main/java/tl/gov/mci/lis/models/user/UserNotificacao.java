package tl.gov.mci.lis.models.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.notificacao.Notificacao;

import java.time.Instant;

@Entity
@Table(name = "lic_user_notificacao")
@Getter
@Setter
public class UserNotificacao extends EntityDB {
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notificacao_id", nullable = false)
    private Notificacao notificacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User destinatario;

    private Boolean visto;
    private Instant vistoEm;
}
