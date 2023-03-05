package com.epam.ld.module2.testing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Mail server class.
 */
public class MailServer {

    /**
     * Send notification.
     *
     * @param addresses  the addresses
     * @param messageContent the message content
     * @throws IOException exception
     */
    public void send(String addresses, String messageContent) throws IOException {
        if (addresses != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(addresses, StandardCharsets.UTF_8))) {
                writer.write(messageContent);
            }
        } else {
            System.out.println(messageContent);
        }
    }
}
