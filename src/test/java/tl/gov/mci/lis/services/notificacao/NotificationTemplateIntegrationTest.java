package tl.gov.mci.lis.services.notificacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tl.gov.mci.lis.dtos.notificacao.NotificationTemplate;
import tl.gov.mci.lis.enums.AplicanteStatus;
import tl.gov.mci.lis.enums.Role;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration-style test to verify the notification template system works end-to-end.
 * Simulates the flow: resolve template -> render with context -> verify output
 */
class NotificationTemplateIntegrationTest {

    private NotificationTemplateResolver resolver;
    private NotificationMessageRenderer renderer;

    @BeforeEach
    void setUp() {
        resolver = new DefaultNotificationTemplateResolver();
        renderer = new NotificationMessageRenderer();
    }

    @Test
    void shouldResolveAndRenderSubmetidoNotificationForCliente() {
        // Given: An application is submitted by a client
        AplicanteStatus status = AplicanteStatus.SUBMETIDO;
        Role role = Role.ROLE_CLIENT;

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");
        context.put("nomeCliente", "João Silva");
        context.put("empresa", "Silva & Filhos Lda");

        // When: Template is resolved and rendered
        NotificationTemplate template = resolver.resolve(status, role);
        NotificationTemplate rendered = renderer.render(template, context);

        // Then: Cliente receives a confirmation message
        assertEquals("Pedido submetido com sucesso", rendered.title());
        assertTrue(rendered.description().contains("APL-2025-001"));
        assertTrue(rendered.description().contains("Recebemos o seu pedido"));
        assertFalse(rendered.description().contains("{processoId}"), "Placeholder should be replaced");
    }

    @Test
    void shouldResolveAndRenderSubmetidoNotificationForChefe() {
        // Given: An application is submitted
        AplicanteStatus status = AplicanteStatus.SUBMETIDO;
        Role role = Role.ROLE_CHIEF;

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");
        context.put("nomeCliente", "João Silva");
        context.put("empresa", "Silva & Filhos Lda");

        // When: Template is resolved and rendered
        NotificationTemplate template = resolver.resolve(status, role);
        NotificationTemplate rendered = renderer.render(template, context);

        // Then: CHEFE receives notification about new application
        assertEquals("Nova aplicação submetida", rendered.title());
        assertTrue(rendered.description().contains("João Silva"));
        assertTrue(rendered.description().contains("APL-2025-001"));
        assertTrue(rendered.description().contains("aguarda revisão"));
    }

    @Test
    void shouldResolveAndRenderAtribuidoNotificationWithResponsavel() {
        // Given: An application is assigned to a staff member
        AplicanteStatus status = AplicanteStatus.ATRIBUIDO;
        Role role = Role.ROLE_CHIEF;

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-042");
        context.put("responsavel", "Maria Santos");
        context.put("nomeCliente", "João Silva");
        context.put("empresa", "Tech Solutions Lda");

        // When: Template is resolved and rendered
        NotificationTemplate template = resolver.resolve(status, role);
        NotificationTemplate rendered = renderer.render(template, context);

        // Then: CHEFE sees who was assigned
        assertEquals("Aplicação atribuída", rendered.title());
        assertTrue(rendered.description().contains("APL-2025-042"));
        assertTrue(rendered.description().contains("Maria Santos"));
        assertFalse(rendered.description().contains("{responsavel}"), "Placeholder should be replaced");
    }

    @Test
    void shouldResolveAndRenderRejeitadoNotificationWithMotivo() {
        // Given: An application is rejected with a reason
        AplicanteStatus status = AplicanteStatus.REJEITADO;
        Role role = Role.ROLE_CLIENT;

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-050");
        context.put("motivo", "Documentação insuficiente para análise.");
        context.put("nomeCliente", "João Silva");
        context.put("empresa", "Example Corp");

        // When: Template is resolved and rendered
        NotificationTemplate template = resolver.resolve(status, role);
        NotificationTemplate rendered = renderer.render(template, context);

        // Then: Cliente receives rejection notification with reason
        assertEquals("Pedido não aprovado", rendered.title());
        assertTrue(rendered.description().contains("APL-2025-050"));
        assertTrue(rendered.description().contains("não foi aprovado"));
        assertTrue(rendered.description().contains("Documentação insuficiente"));
    }

    @Test
    void shouldResolveAndRenderAprovadoNotificationForAllRoles() {
        // Given: An application is approved
        AplicanteStatus status = AplicanteStatus.APROVADO;

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-100");
        context.put("nomeCliente", "João Silva");
        context.put("empresa", "Silva & Filhos Lda");

        // When/Then: All roles receive appropriate approval notifications

        // Cliente
        NotificationTemplate clienteTemplate = resolver.resolve(status, Role.ROLE_CLIENT);
        NotificationTemplate clienteRendered = renderer.render(clienteTemplate, context);
        assertEquals("Pedido aprovado", clienteRendered.title());
        assertTrue(clienteRendered.description().contains("foi aprovado com sucesso"));

        // CHEFE
        NotificationTemplate chefeTemplate = resolver.resolve(status, Role.ROLE_CHIEF);
        NotificationTemplate chefeRendered = renderer.render(chefeTemplate, context);
        assertEquals("Aplicação aprovada", chefeRendered.title());
        assertTrue(chefeRendered.description().contains("aprovado pela direção"));

        // MANAGER
        NotificationTemplate managerTemplate = resolver.resolve(status, Role.ROLE_MANAGER);
        NotificationTemplate managerRendered = renderer.render(managerTemplate, context);
        assertEquals("Aprovação final concluída", managerRendered.title());
        assertTrue(managerRendered.description().contains("processo foi concluído"));
    }

    @Test
    void shouldHandleStatusChangeScenarioFromSubmetidoToRevisto() {
        // Simulate a complete status change scenario
        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-075");
        context.put("nomeCliente", "Ana Costa");
        context.put("empresa", "Costa Trading");

        // Step 1: Application submitted
        NotificationTemplate submetidoCliente = resolver.resolve(AplicanteStatus.SUBMETIDO, Role.ROLE_CLIENT);
        NotificationTemplate renderedSubmetido = renderer.render(submetidoCliente, context);
        assertEquals("Pedido submetido com sucesso", renderedSubmetido.title());

        // Step 2: Application reviewed
        NotificationTemplate revistoCliente = resolver.resolve(AplicanteStatus.REVISTO, Role.ROLE_CLIENT);
        NotificationTemplate renderedRevisto = renderer.render(revistoCliente, context);
        assertEquals("Pedido revisto", renderedRevisto.title());
        assertTrue(renderedRevisto.description().contains("APL-2025-075"));
        assertTrue(renderedRevisto.description().contains("continua em processamento"));
    }

    @Test
    void shouldVerifyAllPlaceholdersAreReplacedCorrectly() {
        // Test with all possible placeholders
        AplicanteStatus status = AplicanteStatus.REJEITADO;
        Role role = Role.ROLE_CLIENT;

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-999");
        context.put("nomeCliente", "Test Client");
        context.put("empresa", "Test Company");
        context.put("responsavel", "Test Staff");
        context.put("motivo", "Test reason for rejection");

        NotificationTemplate template = resolver.resolve(status, role);
        NotificationTemplate rendered = renderer.render(template, context);

        // Verify no placeholders remain
        assertFalse(rendered.title().contains("{"), "Title should not contain placeholders");
        assertFalse(rendered.title().contains("}"), "Title should not contain placeholders");
        assertFalse(rendered.description().contains("{processoId}"), "Description should not contain {processoId}");

        // Verify values are present
        assertTrue(rendered.description().contains("APL-2025-999"));
        assertTrue(rendered.description().contains("Test reason for rejection"));
    }
}

