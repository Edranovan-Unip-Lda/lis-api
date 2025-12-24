package tl.gov.mci.lis.services.notificacao;

import tl.gov.mci.lis.dtos.notificacao.NotificationTemplate;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.Role;

public interface NotificationTemplateResolver {
    NotificationTemplate resolve(AplicanteStatus status, Role role);
}

