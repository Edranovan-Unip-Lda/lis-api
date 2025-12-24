package tl.gov.mci.lis.services.notificacao;

import org.springframework.stereotype.Component;
import tl.gov.mci.lis.dtos.notificacao.NotificationTemplate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders notification templates by replacing placeholders with actual values.
 * Placeholders format: {placeholderName}
 * If a placeholder is not found in the context, it is replaced with an empty string.
 */
@Component
public class NotificationMessageRenderer {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    /**
     * Renders a notification template by replacing placeholders with values from the context.
     *
     * @param template The notification template with placeholders
     * @param context  Map of placeholder names to their values
     * @return A new NotificationTemplate with placeholders replaced
     */
    public NotificationTemplate render(NotificationTemplate template, Map<String, Object> context) {
        String renderedTitle = replacePlaceholders(template.title(), context);
        String renderedDescription = replacePlaceholders(template.description(), context);
        return new NotificationTemplate(renderedTitle, renderedDescription);
    }

    private String replacePlaceholders(String text, Map<String, Object> context) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String placeholder = matcher.group(1);
            Object value = context.get(placeholder);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }

        matcher.appendTail(result);
        return result.toString();
    }
}

