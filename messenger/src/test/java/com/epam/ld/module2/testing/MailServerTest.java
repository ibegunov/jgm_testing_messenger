package com.epam.ld.module2.testing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class MailServerTest {

    @Test
    void testSendToEmailFile() throws IOException {
        String emailFile = "test-emails.txt";
        String messageContent = "Hello World!";

        MailServer mailServer = new MailServer();
        mailServer.send(emailFile, messageContent);

        String actualContent = Files.readString(Paths.get(emailFile));
        assertEquals(messageContent, actualContent);
    }

    @Test
    void testSendToNullAddresses() throws IOException {
        String messageContent = "Hello World!";

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));

        MailServer mailServer = new MailServer();
        mailServer.send(null, messageContent);

        assertEquals(messageContent + "\n", outContent.toString(StandardCharsets.UTF_8));
    }

    @Test
    void testSendToInvalidFile() {
        String emailFile = "/this/is/an/invalid/path/test-emails.txt";
        String messageContent = "Hello World!";

        MailServer mailServer = new MailServer();
        assertThrows(IOException.class, () -> mailServer.send(emailFile, messageContent));
    }

}
