package com.epam.ld.module2.testing.template;

import com.epam.ld.module2.testing.Client;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Template engine.
 */
public class TemplateEngine {
    /**
     * Generate message string.
     *
     * @param template the template
     * @param client   the client
     * @return the string
     */
    public String generateMessage(Template template, Client client) {
            Pattern pattern = Pattern.compile("#\\{(.+?)\\}");
            String templateStr = template.getValue();
            Matcher matcher = pattern.matcher(templateStr);
            StringBuilder result = new StringBuilder();

            Map<String, String> values = client.getParams();

            while (matcher.find()) {
                String value = values.get(matcher.group());
                if (value == null) {
                    throw new IllegalArgumentException("Missing value for " + matcher.group());
                }
                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
            matcher.appendTail(result);
            return result.toString();
    }
}
