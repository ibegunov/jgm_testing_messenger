package com.epam.ld.module2.testing;


import com.epam.ld.module2.testing.template.Template;
import com.epam.ld.module2.testing.template.TemplateEngine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Messenger.
 */
public class Messenger {
    private MailServer mailServer;
    private TemplateEngine templateEngine;

    /**
     * Instantiates a new Messenger.
     *
     * @param mailServer     the mail server
     * @param templateEngine the template engine
     */
    public Messenger(MailServer mailServer,
                     TemplateEngine templateEngine) {
        this.mailServer = mailServer;
        this.templateEngine = templateEngine;
    }

    /**
     * Send message.
     *
     * @param client   the client
     * @param template the template
     * @throws IOException exception
     */
    public void sendMessage(Client client, Template template) throws IOException {
        String messageContent =
            templateEngine.generateMessage(template, client);
        mailServer.send(client.getAddresses(), messageContent);
    }

    /**
     * @param args input params
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        Messenger messenger = new Messenger(new MailServer(), new TemplateEngine());
        // check if file mode is specified
        if (args.length == 2) {
            String inputFile = args[0];
            String outputFile = args[1];
            messenger.sendFromFile(inputFile, outputFile);
        } else {
            // run in console mode
            messenger.sendFromConsole();
        }
    }

    void sendFromConsole() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String templateStr = reader.readLine();

        Template template = new Template();
        template.setValue(templateStr);

        Map<String, String> templateValues = readValuesFromConsole();
        Client client = new Client();
        client.setParams(templateValues);

        sendMessage(client, template);
    }

    void sendFromFile(String inputFile, String outputFile) throws IOException {
        Template template;
        Map<String, String> templateValues;
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile, StandardCharsets.UTF_8))) {
            String templateStr = reader.readLine();

            template = new Template();
            template.setValue(templateStr);

            templateValues = readValuesFromFile(reader);
        }
        Client client = new Client();
        client.setParams(templateValues);
        client.setAddresses(outputFile);

        sendMessage(client, template);
    }

    private static Map<String, String> readValuesFromConsole() throws IOException {
        Map<String, String> values = new HashMap<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String input = reader.readLine();
        while (input != null && !input.equals("")) {
            String[] keyValue = input.split("=");
            values.put("#{" + keyValue[0] + "}", keyValue[1]);
            input = reader.readLine();
        }
        return values;
    }

    private static Map<String, String> readValuesFromFile(BufferedReader reader) throws IOException {
        Map<String, String> values = new HashMap<>();
        String input = reader.readLine();
        while (input != null) {
            String[] keyValue = input.split("=");
            values.put("#{" + keyValue[0] + "}", keyValue[1]);
            input = reader.readLine();
        }
        return values;
    }

}