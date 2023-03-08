package com.epam.ld.module2.testing.template;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.ld.module2.testing.Client;
import com.epam.ld.module2.testing.extension.TestExecutionFileLogger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith(TestExecutionFileLogger.class)
class TemplateEngineTest {

    private TemplateEngine templateEngine;
    private Template template;
    private Client client;

    @BeforeEach
    public void setUp() {
        templateEngine = spy(new TemplateEngine());
        client = new Client();
        template = new Template();
    }

    @Test
    @DisplayName("Template generator should replace variable placeholders")
    void testFillInTemplate() {
        assumeTrue(isJavaVersionHigherThan11());

        String templateString = "Dear #{name},\n\nThank you for contacting us about #{subject}. We will respond to "
                + "your message at #{time}.\n\nSincerely,\nThe Support Team";
        Map<String, String> values = new HashMap<>();
        values.put("#{name}", "John");
        values.put("#{subject}", "your recent order");
        values.put("#{time}", "3:00 PM");

        client.setParams(values);
        template.setValue(templateString);

        // Partial mock example
        templateEngine = mock(TemplateEngine.class);
        when(templateEngine.generateMessage(any(), any())).thenCallRealMethod();
        String filledInTemplate = templateEngine.generateMessage(template, client);

        String expectedOutput = "Dear John,\n\nThank you for contacting us about your recent order. We will respond to your message at 3:00 PM.\n\nSincerely,\nThe Support Team";

        assertEquals(expectedOutput, filledInTemplate);
        verify(templateEngine, times(1)).generateMessage(template, client);
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
    @Tag("example")
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

    @Test
    @Disabled("Disabled test")
    void testDisabled() {
    }

    @ParameterizedTest(name = "{2}")
    @CsvSource(value = {
            "Hello #{name}!;name=John;Hello John!",
            "Hello #{name} #{last_name}!;name=John,last_name=Doe;Hello John Doe!"
    }, delimiter = ';')
    void testMessageGeneration(String templateStr, String paramsStr, String expected) {
        Template template = new Template();
        template.setValue(templateStr);
        Map<String, String> params = stringToParams(paramsStr);
        Client client = new Client();
        client.setParams(params);
        String actual = templateEngine.generateMessage(template, client);
        assertEquals(expected, actual);
    }

    private Map<String, String> stringToParams(String paramsStr) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = paramsStr.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            params.put("#{" + keyValue[0] + "}", keyValue[1]);
        }
        return params;
    }

    @TestFactory
    Stream<DynamicTest> testGenerateMessage() {
        return Stream.of(
                dynamicTest("replace single value", () -> {
                    Template template = new Template();
                    template.setValue("Hello #{name}!");
                    Map<String, String> params = new HashMap<>();
                    params.put("#{name}", "John");
                    Client client = new Client();
                    client.setParams(params);
                    String actual = templateEngine.generateMessage(template, client);
                    assertEquals("Hello John!", actual);
                }));
    }

    private boolean isJavaVersionHigherThan11() {
        String javaVersion = System.getProperty("java.version");
        int majorVersion = Integer.parseInt(javaVersion.split("\\.")[0]);
        return majorVersion >= 11;
    }

}
