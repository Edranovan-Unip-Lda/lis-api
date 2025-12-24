package tl.gov.mci.lis.services.notificacao;

import org.springframework.stereotype.Component;
import tl.gov.mci.lis.dtos.notificacao.NotificationTemplate;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.Role;

import java.util.EnumMap;
import java.util.Map;

@Component
public class DefaultNotificationTemplateResolver implements NotificationTemplateResolver {

    private final Map<AplicanteStatus, Map<Role, NotificationTemplate>> templates;

    public DefaultNotificationTemplateResolver() {
        this.templates = new EnumMap<>(AplicanteStatus.class);
        initializeTemplates();
    }

    private void initializeTemplates() {
        // SUBMETIDO
        Map<Role, NotificationTemplate> submetidoTemplates = new EnumMap<>(Role.class);
        submetidoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Aplicante submetido com sucesso",
                "Recebemos o seu pedido {processoId}. Iremos analisá-lo brevemente."
        ));
        submetidoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Novo Aplicante submetido",
                "O cliente {nomeCliente} submeteu o aplicante {processoId} e aguarda revisão."
        ));
        submetidoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Aplicante submetido para verificação",
                "Foi submetido o aplicante {processoId} e encontra-se em verificação pelo supervisor."
        ));
        templates.put(AplicanteStatus.SUBMETIDO, submetidoTemplates);

        // REVISTO
        Map<Role, NotificationTemplate> revistoTemplates = new EnumMap<>(Role.class);
        revistoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Aplicante revisto",
                "O seu aplicante {processoId} foi revisto e continua em processamento."
        ));
        revistoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Revisão concluída",
                "A revisão do aplicante {processoId} foi concluída com sucesso."
        ));
        revistoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Aplicante revisto pelo supervisor",
                "O aplicante {processoId} foi revisto e está pronto para decisão."
        ));
        templates.put(AplicanteStatus.REVISTO, revistoTemplates);

        // REVISAO (same as REVISTO - under review)
        Map<Role, NotificationTemplate> revisaoTemplates = new EnumMap<>(Role.class);
        revisaoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Aplicante em revisão",
                "O seu aplicante {processoId} está em revisão e continua em processamento."
        ));
        revisaoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Aplicante em revisão",
                "O aplicante {processoId} está em fase de revisão."
        ));
        revisaoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Aplicante em revisão pelo supervisor",
                "O aplicante {processoId} está sendo revisto pelo supervisor."
        ));
        templates.put(AplicanteStatus.REVISAO, revisaoTemplates);

        // ATRIBUIDO
        Map<Role, NotificationTemplate> atribuidoTemplates = new EnumMap<>(Role.class);
        atribuidoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Aplicante em tratamento",
                "O seu aplicante {processoId} foi atribuído para continuação do processo."
        ));
        atribuidoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Aplicante atribuído",
                "O aplicante {processoId} foi atribuído ao responsável {responsavel}."
        ));
        atribuidoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Aplicante atribuído pelo supervisor",
                "O supervisor atribuiu o aplicante {processoId} para seguimento."
        ));
        templates.put(AplicanteStatus.ATRIBUIDO, atribuidoTemplates);

        // REJEITADO
        Map<Role, NotificationTemplate> rejeitadoTemplates = new EnumMap<>(Role.class);
        rejeitadoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Aplicante não aprovado",
                "O aplicante {processoId} não foi aprovado. Consulte os detalhes para mais informações. {motivo}"
        ));
        rejeitadoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Aplicante rejeitado",
                "O aplicante {processoId} foi rejeitado após análise."
        ));
        rejeitadoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Decisão de rejeição registada",
                "Foi registada a rejeição do aplicante {processoId} no sistema."
        ));
        templates.put(AplicanteStatus.REJEITADO, rejeitadoTemplates);

        // APROVADO
        Map<Role, NotificationTemplate> aprovadoTemplates = new EnumMap<>(Role.class);
        aprovadoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Aplicante aprovado",
                "O seu aplicante {processoId} foi aprovado com sucesso."
        ));
        aprovadoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Aplicante aprovada",
                "O aplicante {processoId} foi aprovado pela direção."
        ));
        aprovadoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Aprovação final concluída",
                "O aplicante {processoId} foi aprovado e o processo foi concluído."
        ));
        templates.put(AplicanteStatus.APROVADO, aprovadoTemplates);

        // EM_CURSO (Application in progress)
        Map<Role, NotificationTemplate> emCursoTemplates = new EnumMap<>(Role.class);
        emCursoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Pedido em curso",
                "O seu pedido {processoId} está em curso de processamento."
        ));
        emCursoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Aplicação em curso",
                "O pedido {processoId} está em curso de processamento."
        ));
        emCursoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Processo em curso",
                "O pedido {processoId} está em curso de processamento."
        ));
        templates.put(AplicanteStatus.EM_CURSO, emCursoTemplates);

        // SUSPENDE (Application suspended)
        Map<Role, NotificationTemplate> suspendeTemplates = new EnumMap<>(Role.class);
        suspendeTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Pedido suspenso",
                "O seu pedido {processoId} foi temporariamente suspenso. {motivo}"
        ));
        suspendeTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Aplicação suspensa",
                "O pedido {processoId} foi suspenso temporariamente."
        ));
        suspendeTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Processo suspenso",
                "O pedido {processoId} foi suspenso temporariamente."
        ));
        templates.put(AplicanteStatus.SUSPENDE, suspendeTemplates);

        // EXPIRADO (Application expired)
        Map<Role, NotificationTemplate> expiradoTemplates = new EnumMap<>(Role.class);
        expiradoTemplates.put(Role.ROLE_CLIENT, new NotificationTemplate(
                "Aplicante expirado",
                "O seu aplicante {processoId} expirou. Por favor, contacte-nos para mais informações."
        ));
        expiradoTemplates.put(Role.ROLE_CHIEF, new NotificationTemplate(
                "Aplicante expirada",
                "O aplicante {processoId} expirou e necessita de atenção."
        ));
        expiradoTemplates.put(Role.ROLE_MANAGER, new NotificationTemplate(
                "Aplicante expirado",
                "O aplicante {processoId} expirou no sistema."
        ));
        templates.put(AplicanteStatus.EXPIRADO, expiradoTemplates);
    }

    @Override
    public NotificationTemplate resolve(AplicanteStatus status, Role role) {
        Map<Role, NotificationTemplate> statusTemplates = templates.get(status);
        if (statusTemplates == null) {
            throw new IllegalArgumentException("No templates found for status: " + status);
        }

        NotificationTemplate template = statusTemplates.get(role);
        if (template == null) {
            throw new IllegalArgumentException("No template found for status: " + status + " and role: " + role);
        }

        return template;
    }
}

