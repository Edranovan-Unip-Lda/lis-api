package tl.gov.mci.lis.services.notificacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tl.gov.mci.lis.dtos.notificacao.NotificationTemplate;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.Role;

import static org.junit.jupiter.api.Assertions.*;

class DefaultNotificationTemplateResolverTest {

    private DefaultNotificationTemplateResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DefaultNotificationTemplateResolver();
    }

    @Test
    void shouldResolveTemplateForSubmetidoCliente() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.SUBMETIDO, Role.ROLE_CLIENT);

        assertNotNull(template);
        assertEquals("Pedido submetido com sucesso", template.title());
        assertTrue(template.description().contains("Recebemos o seu pedido"));
    }

    @Test
    void shouldResolveTemplateForSubmetidoChefe() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.SUBMETIDO, Role.ROLE_CHIEF);

        assertNotNull(template);
        assertEquals("Nova aplicação submetida", template.title());
        assertTrue(template.description().contains("submeteu o pedido"));
    }

    @Test
    void shouldResolveTemplateForSubmetidoDiretor() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.SUBMETIDO, Role.ROLE_MANAGER);

        assertNotNull(template);
        assertEquals("Aplicação submetida para verificação", template.title());
        assertTrue(template.description().contains("verificação pelo supervisor"));
    }

    @Test
    void shouldResolveTemplateForRevistoCliente() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REVISTO, Role.ROLE_CLIENT);

        assertNotNull(template);
        assertEquals("Pedido revisto", template.title());
        assertTrue(template.description().contains("foi revisto"));
    }

    @Test
    void shouldResolveTemplateForRevistoChefe() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REVISTO, Role.ROLE_CHIEF);

        assertNotNull(template);
        assertEquals("Revisão concluída", template.title());
    }

    @Test
    void shouldResolveTemplateForRevisaoCliente() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REVISAO, Role.ROLE_CLIENT);

        assertNotNull(template);
        assertEquals("Pedido em revisão", template.title());
        assertTrue(template.description().contains("está em revisão"));
    }

    @Test
    void shouldResolveTemplateForRevisaoChefe() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REVISAO, Role.ROLE_CHIEF);

        assertNotNull(template);
        assertEquals("Aplicação em revisão", template.title());
    }

    @Test
    void shouldResolveTemplateForRevistoDiretor() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REVISTO, Role.ROLE_MANAGER);

        assertNotNull(template);
        assertEquals("Aplicação revista pelo supervisor", template.title());
    }

    @Test
    void shouldResolveTemplateForAtribuidoCliente() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.ATRIBUIDO, Role.ROLE_CLIENT);

        assertNotNull(template);
        assertEquals("Pedido em tratamento", template.title());
        assertTrue(template.description().contains("atribuído"));
    }

    @Test
    void shouldResolveTemplateForAtribuidoChefe() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.ATRIBUIDO, Role.ROLE_CHIEF);

        assertNotNull(template);
        assertEquals("Aplicação atribuída", template.title());
        assertTrue(template.description().contains("{responsavel}"));
    }

    @Test
    void shouldResolveTemplateForAtribuidoDiretor() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.ATRIBUIDO, Role.ROLE_MANAGER);

        assertNotNull(template);
        assertEquals("Processo atribuído pelo supervisor", template.title());
    }

    @Test
    void shouldResolveTemplateForRejeitadoCliente() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REJEITADO, Role.ROLE_CLIENT);

        assertNotNull(template);
        assertEquals("Pedido não aprovado", template.title());
        assertTrue(template.description().contains("não foi aprovado"));
        assertTrue(template.description().contains("{motivo}"));
    }

    @Test
    void shouldResolveTemplateForRejeitadoChefe() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REJEITADO, Role.ROLE_CHIEF);

        assertNotNull(template);
        assertEquals("Aplicação rejeitada", template.title());
    }

    @Test
    void shouldResolveTemplateForRejeitadoDiretor() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.REJEITADO, Role.ROLE_MANAGER);

        assertNotNull(template);
        assertEquals("Decisão de rejeição registada", template.title());
    }

    @Test
    void shouldResolveTemplateForAprovadoCliente() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.APROVADO, Role.ROLE_CLIENT);

        assertNotNull(template);
        assertEquals("Pedido aprovado", template.title());
        assertTrue(template.description().contains("foi aprovado com sucesso"));
    }

    @Test
    void shouldResolveTemplateForAprovadoChefe() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.APROVADO, Role.ROLE_CHIEF);

        assertNotNull(template);
        assertEquals("Aplicação aprovada", template.title());
        assertTrue(template.description().contains("aprovado pela direção"));
    }

    @Test
    void shouldResolveTemplateForAprovadoDiretor() {
        NotificationTemplate template = resolver.resolve(AplicanteStatus.APROVADO, Role.ROLE_MANAGER);

        assertNotNull(template);
        assertEquals("Aprovação final concluída", template.title());
    }

    @Test
    void shouldHaveTemplatesForAllStatusAndRoleCombinations() {
        // Verify that every status has templates for all 3 roles
        AplicanteStatus[] statuses = {
            AplicanteStatus.SUBMETIDO,
            AplicanteStatus.REVISAO,    // ← Added
            AplicanteStatus.REVISTO,
            AplicanteStatus.ATRIBUIDO,
            AplicanteStatus.REJEITADO,
            AplicanteStatus.APROVADO,
            AplicanteStatus.EM_CURSO,   // ← Added
            AplicanteStatus.SUSPENDE,   // ← Added
            AplicanteStatus.EXPIRADO    // ← Added
        };

        Role[] roles = {
            Role.ROLE_CLIENT,
            Role.ROLE_CHIEF,
            Role.ROLE_MANAGER
        };

        for (AplicanteStatus status : statuses) {
            for (Role role : roles) {
                NotificationTemplate template = resolver.resolve(status, role);
                assertNotNull(template,
                    String.format("Template missing for status=%s, role=%s", status, role));
                assertNotNull(template.title(),
                    String.format("Title missing for status=%s, role=%s", status, role));
                assertNotNull(template.description(),
                    String.format("Description missing for status=%s, role=%s", status, role));
            }
        }
    }
}

