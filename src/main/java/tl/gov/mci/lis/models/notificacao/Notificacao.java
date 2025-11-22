package tl.gov.mci.lis.models.notificacao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.models.EntityDB;
import tl.gov.mci.lis.models.aplicante.Aplicante;
import tl.gov.mci.lis.models.user.UserNotificacao;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lic_notificacao")
@Getter
@Setter
public class Notificacao extends EntityDB {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aplicante_id", nullable = false)
    private Aplicante aplicante;

    @Enumerated(EnumType.STRING)
    private AplicanteStatus aplicanteStatus;

    @OneToMany(mappedBy = "notificacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserNotificacao> destinatarios = new ArrayList<>();

    public void addDestinatario(UserNotificacao userNotificacao) {
        destinatarios.add(userNotificacao);
        userNotificacao.setNotificacao(this);
    }
}
