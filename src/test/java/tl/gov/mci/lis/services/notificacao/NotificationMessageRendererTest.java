package tl.gov.mci.lis.services.notificacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tl.gov.mci.lis.dtos.notificacao.NotificationTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationMessageRendererTest {

    private NotificationMessageRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new NotificationMessageRenderer();
    }

    @Test
    void shouldReplaceSinglePlaceholder() {
        NotificationTemplate template = new NotificationTemplate(
            "Pedido {processoId}",
            "O pedido {processoId} foi submetido."
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Pedido APL-2025-001", rendered.title());
        assertEquals("O pedido APL-2025-001 foi submetido.", rendered.description());
    }

    @Test
    void shouldReplaceMultiplePlaceholders() {
        NotificationTemplate template = new NotificationTemplate(
            "Pedido {processoId}",
            "O cliente {nomeCliente} da empresa {empresa} submeteu o pedido {processoId}."
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");
        context.put("nomeCliente", "João Silva");
        context.put("empresa", "Silva & Filhos Lda");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Pedido APL-2025-001", rendered.title());
        assertEquals("O cliente João Silva da empresa Silva & Filhos Lda submeteu o pedido APL-2025-001.",
            rendered.description());
    }

    @Test
    void shouldReplaceWithEmptyStringWhenPlaceholderNotInContext() {
        NotificationTemplate template = new NotificationTemplate(
            "Pedido {processoId}",
            "O pedido foi atribuído ao {responsavel}."
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");
        // responsavel is missing

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Pedido APL-2025-001", rendered.title());
        assertEquals("O pedido foi atribuído ao .", rendered.description());
    }

    @Test
    void shouldHandleNullContext() {
        NotificationTemplate template = new NotificationTemplate(
            "Pedido {processoId}",
            "Descrição {processoId}"
        );

        NotificationTemplate rendered = renderer.render(template, new HashMap<>());

        assertEquals("Pedido ", rendered.title());
        assertEquals("Descrição ", rendered.description());
    }

    @Test
    void shouldHandleTemplateWithNoPlaceholders() {
        NotificationTemplate template = new NotificationTemplate(
            "Pedido submetido",
            "O pedido foi submetido com sucesso."
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Pedido submetido", rendered.title());
        assertEquals("O pedido foi submetido com sucesso.", rendered.description());
    }

    @Test
    void shouldHandleEmptyTemplate() {
        NotificationTemplate template = new NotificationTemplate("", "");

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("", rendered.title());
        assertEquals("", rendered.description());
    }

    @Test
    void shouldHandleSpecialCharactersInValues() {
        NotificationTemplate template = new NotificationTemplate(
            "Pedido {processoId}",
            "Motivo: {motivo}"
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");
        context.put("motivo", "Documentação incompleta: faltam certidões (CRC, NIF) & declarações.");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Pedido APL-2025-001", rendered.title());
        assertEquals("Motivo: Documentação incompleta: faltam certidões (CRC, NIF) & declarações.",
            rendered.description());
    }

    @Test
    void shouldHandleSamePlaceholderMultipleTimes() {
        NotificationTemplate template = new NotificationTemplate(
            "Pedido {processoId}",
            "O pedido {processoId} foi revisto. Consulte o pedido {processoId} no sistema."
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-001");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Pedido APL-2025-001", rendered.title());
        assertEquals("O pedido APL-2025-001 foi revisto. Consulte o pedido APL-2025-001 no sistema.",
            rendered.description());
    }

    @Test
    void shouldRenderRealWorldScenario() {
        // Simulate ATRIBUIDO notification for CHEFE with responsavel
        NotificationTemplate template = new NotificationTemplate(
            "Aplicação atribuída",
            "O pedido {processoId} foi atribuído ao responsável {responsavel}."
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-042");
        context.put("responsavel", "Maria Santos");
        context.put("nomeCliente", "João Silva");
        context.put("empresa", "Tech Solutions Lda");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Aplicação atribuída", rendered.title());
        assertEquals("O pedido APL-2025-042 foi atribuído ao responsável Maria Santos.",
            rendered.description());
    }

    @Test
    void shouldRenderRejeitadoWithMotivo() {
        // Simulate REJEITADO notification for CLIENTE with motivo
        NotificationTemplate template = new NotificationTemplate(
            "Pedido não aprovado",
            "O pedido {processoId} não foi aprovado. Consulte os detalhes para mais informações. {motivo}"
        );

        Map<String, Object> context = new HashMap<>();
        context.put("processoId", "APL-2025-050");
        context.put("motivo", "Documentação insuficiente para análise.");

        NotificationTemplate rendered = renderer.render(template, context);

        assertEquals("Pedido não aprovado", rendered.title());
        assertTrue(rendered.description().contains("APL-2025-050"));
        assertTrue(rendered.description().contains("Documentação insuficiente"));
    }
}

