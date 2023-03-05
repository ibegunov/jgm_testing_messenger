package com.epam.ld.module2.testing.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.epam.ld.module2.testing.Client;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TemplateEngineTest {

    private TemplateEngine templateEngine;
    private Template template;
    private Client client;

    @BeforeEach
    public void setUp() {
        templateEngine = new TemplateEngine();
        client = new Client();
        template = new Template();
    }

    @Test
    @DisplayName("Template generator should replace variable placeholders")
    void testFillInTemplate() {
        String templateString = "Dear #{name},\n\nThank you for contacting us about #{subject}. We will respond to "
                + "your message at #{time}.\n\nSincerely,\nThe Support Team";
        Map<String, String> values = new HashMap<>();
        values.put("#{name}", "John");
        values.put("#{subject}", "your recent order");
        values.put("#{time}", "3:00 PM");

        client.setParams(values);
        template.setValue(templateString);
        String filledInTemplate = templateEngine.generateMessage(template, client);

        String expectedOutput = "Dear John,\n\nThank you for contacting us about your recent order. We will respond to your message at 3:00 PM.\n\nSincerely,\nThe Support Team";

        assertEquals(expectedOutput, filledInTemplate);
    }

    @Test
    @DisplayName("Template generator should throw exceptions if values are missing")
    void testFillInTemplateMissingValueThrowsException() {
        String templateString = "Dear #{name},\n\nThank you for contacting us about #{subject}. We will respond to "
                + "your message at #{time}.\n\nSincerely,\nThe Support Team";
        Map<String, String> values = new HashMap<>();
        values.put("#{name}", "John");
        values.put("#{time}", "3:00 PM");

        client.setParams(values);
        template.setValue(templateString);

        assertThrows(RuntimeException.class, () -> templateEngine.generateMessage(template, client));
    }

    @Test
    @DisplayName("Template generator should ignore extra values")
    void testFillInTemplateIgnoresExtraValues() {
        String templateString = "Dear #{name},\n\nThank you for contacting us about #{subject}. We will respond to "
                + "your message at #{time}.\n\nSincerely,\nThe Support Team";
        Map<String, String> values = new HashMap<>();
        values.put("#{name}", "John");
        values.put("#{subject}", "your recent order");
        values.put("#{time}", "3:00 PM");
        values.put("#{extraValue}", "This value should be ignored");

        client.setParams(values);
        template.setValue(templateString);

        String filledInTemplate = templateEngine.generateMessage(template, client);

        String expectedOutput = "Dear John,\n\nThank you for contacting us about your recent order. We will respond to your message at 3:00 PM.\n\nSincerely,\nThe Support Team";
        assertEquals(expectedOutput, filledInTemplate);
    }

    @Test
    @DisplayName("Template generator should replace placeholder to placeholder")
    void testFillInTemplateSupportsRuntimePlaceholders() {
        String templateString = "Some text: #{value}";
        Map<String, String> values = new HashMap<>();
        values.put("#{value}", "#{tag}");

        client.setParams(values);
        template.setValue(templateString);

        String filledInTemplate = templateEngine.generateMessage(template, client);

        String expectedOutput = "Some text: #{tag}";
        assertEquals(expectedOutput, filledInTemplate);
    }

    @Test
    @DisplayName("Template generator should support latin characters")
    void testFillInTemplateSupportsLatin1CharacterSet() {
        String templateString = "Hello #{name}, how are you today? é";
        Map<String, String> values = new HashMap<>();
        values.put("#{name}", "John");

        client.setParams(values);
        template.setValue(templateString);

        String filledInTemplate = templateEngine.generateMessage(template, client);

        String expectedOutput = "Hello John, how are you today? é";
        assertEquals(expectedOutput, filledInTemplate);
    }

}
