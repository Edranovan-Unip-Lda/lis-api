# Notification Template System - Developer Guide

## Quick Start

### Using the Template System

The notification template system automatically selects and renders appropriate messages based on:
1. **Application Status** (SUBMETIDO, REVISTO, ATRIBUIDO, REJEITADO, APROVADO)
2. **User Role** (ROLE_CLIENT, ROLE_CHIEF, ROLE_MANAGER)

### Basic Usage in Service

```java
@Service
@RequiredArgsConstructor
public class YourService {
    private final NotificationTemplateResolver templateResolver;
    private final NotificationMessageRenderer messageRenderer;
    
    public void sendNotification(Aplicante aplicante, User user, Role role) {
        // 1. Build context with dynamic values
        Map<String, Object> context = new HashMap<>();
        context.put("processoId", aplicante.getNumero());
        context.put("nomeCliente", user.getName());
        context.put("empresa", aplicante.getEmpresa().getNome());
        
        // 2. Resolve template based on status and role
        NotificationTemplate template = templateResolver.resolve(
            aplicante.getEstado(), 
            role  // ROLE_CLIENT, ROLE_CHIEF, or ROLE_MANAGER
        );
        
        // 3. Render template with actual values
        NotificationTemplate rendered = messageRenderer.render(template, context);
        
        // 4. Use rendered content
        String title = rendered.title();
        String description = rendered.description();
        
        // Create notification, send email, etc.
    }
}
```

## Available Placeholders

| Placeholder | Type | Usage | Example |
|------------|------|-------|---------|
| `{processoId}` | String | Application/process number | "APL-2025-001" |
| `{nomeCliente}` | String | Client's full name | "João Silva" |
| `{empresa}` | String | Company name | "Silva & Filhos Lda" |
| `{responsavel}` | String | Staff member assigned | "Maria Santos" |
| `{motivo}` | String | Rejection reason | "Documentação incompleta" |

### Adding New Placeholders

1. Add the value to your context map:
```java
context.put("newPlaceholder", value);
```

2. Use in templates:
```java
"Your message with {newPlaceholder} here."
```

3. Missing placeholders automatically become empty strings (safe).

## Template Matrix

### SUBMETIDO (Application Submitted)

**Trigger:** Client submits new application

| Role | Title | Key Message |
|----------|-------|-------------|
| ROLE_CLIENT | "Pedido submetido com sucesso" | Confirmation received |
| ROLE_CHIEF | "Nova aplicação submetida" | New application needs review |
| ROLE_MANAGER | "Aplicação submetida para verificação" | Under supervisor verification |

**Required Context:**
- `processoId`
- `nomeCliente` (for ROLE_CHIEF)

---

### REVISTO (Application Reviewed)

**Trigger:** CHIEF reviews application

| Role | Title | Key Message |
|----------|-------|-------------|
| ROLE_CLIENT | "Pedido revisto" | Under processing |
| ROLE_CHIEF | "Revisão concluída" | Review completed |
| ROLE_MANAGER | "Aplicação revista pelo supervisor" | Ready for decision |

**Required Context:**
- `processoId`

---

### ATRIBUIDO (Application Assigned)

**Trigger:** CHIEF assigns to staff member

| Role | Title | Key Message |
|----------|-------|-------------|
| ROLE_CLIENT | "Pedido em tratamento" | Assignment confirmed |
| ROLE_CHIEF | "Aplicação atribuída" | Shows who was assigned |
| ROLE_MANAGER | "Processo atribuído pelo supervisor" | Assignment notification |

**Required Context:**
- `processoId`
- `responsavel` (name of assigned staff)

**Note:** ROLE_CHIEF template uses `{responsavel}` placeholder.

---

### REJEITADO (Application Rejected)

**Trigger:** CHIEF or MANAGER rejects application

| Role | Title | Key Message |
|----------|-------|-------------|
| ROLE_CLIENT | "Pedido não aprovado" | Not approved + reason |
| ROLE_CHIEF | "Aplicação rejeitada" | Rejection logged |
| ROLE_MANAGER | "Decisão de rejeição registada" | Decision registered |

**Required Context:**
- `processoId`
- `motivo` (optional, rejection reason)

**Note:** ROLE_CLIENT template includes `{motivo}` placeholder for rejection reason.

---

### APROVADO (Application Approved)

**Trigger:** MANAGER approves application

| Role | Title | Key Message |
|----------|-------|-------------|
| ROLE_CLIENT | "Pedido aprovado" | Success notification |
| ROLE_CHIEF | "Aplicação aprovada" | Director approval confirmed |
| ROLE_MANAGER | "Aprovação final concluída" | Process completed |

**Required Context:**
- `processoId`

---

## Context Builder Pattern

Use this helper in NotificacaoService:

```java
private Map<String, Object> buildNotificationContext(
    Aplicante aplicante, 
    String responsavel, 
    String motivo
) {
    Map<String, Object> context = new HashMap<>();
    context.put("processoId", aplicante.getNumero());
    context.put("nomeCliente", aplicante.getEmpresa().getUtilizador().getName());
    context.put("empresa", aplicante.getEmpresa().getNome());
    
    if (responsavel != null) {
        context.put("responsavel", responsavel);
    }
    
    if (motivo != null && !motivo.isEmpty()) {
        context.put("motivo", motivo);
    }
    
    return context;
}
```

## Complete Flow Example

### Scenario: Client Submits Application

```java
@Transactional
public void onApplicationSubmitted(Aplicante aplicante) {
    // Application is now SUBMETIDO
    aplicante.setEstado(AplicanteStatus.SUBMETIDO);
    
    // Build context
    Map<String, Object> context = buildNotificationContext(aplicante, null, null);
    
    // Notify CLIENT
    NotificationTemplate clienteTemplate = templateResolver.resolve(
        AplicanteStatus.SUBMETIDO, 
        Role.ROLE_CLIENT
    );
    NotificationTemplate clienteRendered = messageRenderer.render(clienteTemplate, context);
    createUserNotification(aplicante, cliente, clienteRendered);
    
    // Notify CHIEF
    NotificationTemplate chefeTemplate = templateResolver.resolve(
        AplicanteStatus.SUBMETIDO, 
        Role.ROLE_CHIEF
    );
    NotificationTemplate chefeRendered = messageRenderer.render(chefeTemplate, context);
    createUserNotification(aplicante, chefe, chefeRendered);
    
    // Notify MANAGER
    NotificationTemplate managerTemplate = templateResolver.resolve(
        AplicanteStatus.SUBMETIDO, 
        Role.ROLE_MANAGER
    );
    NotificationTemplate managerRendered = messageRenderer.render(managerTemplate, context);
    createUserNotification(aplicante, manager, managerRendered);
}
```

**Result:**
- CLIENT receives: "Pedido submetido com sucesso" / "Recebemos o seu pedido APL-2025-001..."
- CHIEF receives: "Nova aplicação submetida" / "O cliente João Silva submeteu o pedido APL-2025-001..."
- MANAGER receives: "Aplicação submetida para verificação" / "Foi submetido o pedido APL-2025-001..."

### Scenario: CHIEF Assigns to Staff

```java
@Transactional
public void onApplicationAssigned(Aplicante aplicante, User staff) {
    // Application is now ATRIBUIDO
    aplicante.setEstado(AplicanteStatus.ATRIBUIDO);
    
    // Build context with staff name
    Map<String, Object> context = buildNotificationContext(
        aplicante, 
        staff.getName(),  // responsavel
        null
    );
    
    // Notify CHIEF (shows who was assigned)
    NotificationTemplate chefeTemplate = templateResolver.resolve(
        AplicanteStatus.ATRIBUIDO, 
        Role.ROLE_CHIEF
    );
    NotificationTemplate chefeRendered = messageRenderer.render(chefeTemplate, context);
    // "O pedido APL-2025-001 foi atribuído ao responsável Maria Santos."
}
```

### Scenario: Application Rejected with Reason

```java
@Transactional
public void onApplicationRejected(Aplicante aplicante, String reason) {
    // Application is now REJEITADO
    aplicante.setEstado(AplicanteStatus.REJEITADO);
    
    // Build context with rejection reason
    Map<String, Object> context = buildNotificationContext(
        aplicante, 
        null,
        reason  // motivo
    );
    
    // Notify CLIENT (includes reason)
    NotificationTemplate clienteTemplate = templateResolver.resolve(
        AplicanteStatus.REJEITADO, 
        Role.ROLE_CLIENT
    );
    NotificationTemplate clienteRendered = messageRenderer.render(clienteTemplate, context);
    // "O pedido APL-2025-001 não foi aprovado... Documentação incompleta."
}
```

## Testing

### Unit Test Example

```java
@Test
void shouldRenderTemplateWithPlaceholders() {
    // Given
    NotificationTemplate template = new NotificationTemplate(
        "Pedido {processoId}",
        "Cliente {nomeCliente} da empresa {empresa}"
    );
    
    Map<String, Object> context = Map.of(
        "processoId", "APL-2025-001",
        "nomeCliente", "João Silva",
        "empresa", "Silva Lda"
    );
    
    // When
    NotificationTemplate rendered = messageRenderer.render(template, context);
    
    // Then
    assertEquals("Pedido APL-2025-001", rendered.title());
    assertEquals("Cliente João Silva da empresa Silva Lda", rendered.description());
}
```

### Integration Test Example

```java
@Test
void shouldHandleCompleteNotificationFlow() {
    // Simulate SUBMETIDO → REVISTO → APROVADO flow
    Map<String, Object> context = Map.of(
        "processoId", "APL-2025-001",
        "nomeCliente", "João Silva",
        "empresa", "Silva Lda"
    );
    
    // Step 1: SUBMETIDO
    NotificationTemplate submetido = templateResolver.resolve(
        AplicanteStatus.SUBMETIDO, 
        Role.ROLE_CLIENT
    );
    NotificationTemplate rendered1 = messageRenderer.render(submetido, context);
    assertEquals("Pedido submetido com sucesso", rendered1.title());
    
    // Step 2: REVISTO
    NotificationTemplate revisto = templateResolver.resolve(
        AplicanteStatus.REVISTO, 
        Role.ROLE_CLIENT
    );
    NotificationTemplate rendered2 = messageRenderer.render(revisto, context);
    assertEquals("Pedido revisto", rendered2.title());
    
    // Step 3: APROVADO
    NotificationTemplate aprovado = templateResolver.resolve(
        AplicanteStatus.APROVADO, 
        Role.ROLE_CLIENT
    );
    NotificationTemplate rendered3 = messageRenderer.render(aprovado, context);
    assertEquals("Pedido aprovado", rendered3.title());
}
```

## Extending the System

### Adding a New Status

1. **Add to AplicanteStatus enum** (if not exists)

2. **Add templates in DefaultNotificationTemplateResolver:**

```java
// In initializeTemplates()
Map<NotificationAudience, NotificationTemplate> newStatusTemplates = new EnumMap<>(NotificationAudience.class);

newStatusTemplates.put(NotificationAudience.CLIENTE, new NotificationTemplate(
    "Your Title",
    "Your description with {placeholders}"
));

newStatusTemplates.put(NotificationAudience.CHEFE, new NotificationTemplate(
    "Your Title",
    "Your description"
));

newStatusTemplates.put(NotificationAudience.DIRETOR, new NotificationTemplate(
    "Your Title",
    "Your description"
));

templates.put(AplicanteStatus.NEW_STATUS, newStatusTemplates);
```

3. **Add tests** in test classes

### Modifying Existing Templates

Simply update the strings in `DefaultNotificationTemplateResolver.initializeTemplates()`:

```java
submetidoTemplates.put(NotificationAudience.CLIENTE, new NotificationTemplate(
    "Updated Title",  // ← Change here
    "Updated description with {processoId}"  // ← Or here
));
```

No changes to service logic needed!

## Best Practices

1. **Always provide all required placeholders** in context
2. **Use buildNotificationContext()** helper for consistency
3. **Handle optional placeholders** (responsavel, motivo) with null checks
4. **Test template changes** before deploying
5. **Keep messages professional** but user-friendly
6. **Use Portuguese (Portugal)** standard language
7. **Don't hardcode messages** in service logic - use templates

## Troubleshooting

### Placeholder not replaced?
- Check that placeholder name in template matches context key exactly
- Verify context.put() is called before rendering
- Missing placeholders become empty strings (by design)

### IllegalArgumentException: "No template found"?
- Verify the status exists in DefaultNotificationTemplateResolver
- Check all three audiences (CLIENTE, CHEFE, DIRETOR) have templates
- Ensure initializeTemplates() added the status

### Wrong message language?
- All templates are in Portuguese (Portugal)
- Modify in DefaultNotificationTemplateResolver if needed

## Performance Notes

- Templates are loaded once at application startup (in constructor)
- EnumMap provides O(1) lookup time
- Placeholder rendering is done on-demand (only when notification is created)
- No database queries for template retrieval

## Summary

This template system provides:
- ✅ Centralized message management
- ✅ Automatic personalization with placeholders
- ✅ Role-based messaging (CLIENTE, CHEFE, DIRETOR)
- ✅ Status-aware notifications
- ✅ Easy extensibility
- ✅ Type-safe implementation
- ✅ Comprehensive test coverage
- ✅ Professional Portuguese messages

