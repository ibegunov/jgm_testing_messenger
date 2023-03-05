package com.epam.ld.module2.testing;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.ld.module2.testing.template.Template;
import com.epam.ld.module2.testing.template.TemplateEngine;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class MessengerTest {


    @Test
    void testSendFromConsole() throws IOException {
        String templateStr = "Hello #{name}, your order #{order} is confirmed.";
        ByteArrayInputStream inContent = new ByteArrayInputStream(String.join("\n", templateStr,
                "name=John",
                "order=12345",
                "").getBytes(StandardCharsets.UTF_8));
        System.setIn(inContent);

        MailServer mailServer = mock(MailServer.class);
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        when(templateEngine.generateMessage(any(), any()))
                .thenReturn("Hello John, your order 12345 is confirmed.");

        Messenger messenger = new Messenger(mailServer, templateEngine);
        messenger.sendFromConsole();

        verify(mailServer).send(null, "Hello John, your order 12345 is confirmed.");
    }

    @Test
    void testSendFromFile() throws IOException {
        String inputFile = "test-input.txt";
        String outputFile = "test-output.txt";
        String templateStr = "Hello #{name}, your order #{order} is confirmed.";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(inputFile, StandardCharsets.UTF_8))) {
            writer.write(String.join("\n", templateStr,
                    "name=John",
                    "order=12345"));
        }

        MailServer mailServer = mock(MailServer.class);
        TemplateEngine templateEngine = mock(TemplateEngine.class);
        when(templateEngine.generateMessage(any(Template.class), any(Client.class)))
                .thenReturn("Hello John, your order 12345 is confirmed.");

        Messenger messenger = new Messenger(mailServer, templateEngine);
        messenger.sendFromFile(inputFile, outputFile);

        verify(mailServer).send(outputFile, "Hello John, your order 12345 is confirmed.");

        Files.deleteIfExists(Paths.get(inputFile));
        Files.deleteIfExists(Paths.get(outputFile));
    }

}
