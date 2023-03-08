package com.epam.ld.module2.testing.extension;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class TestExecutionFileLogger implements BeforeEachCallback, AfterEachCallback {

    private BufferedWriter writer;

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        File file = new File("test_log.txt");
        writer = new BufferedWriter(new FileWriter(file, true));

        String testName = context.getRequiredTestMethod().getName();
        writer.write("Test started: " + testName + " at " + LocalDateTime.now() + "\n");
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        String testName = context.getRequiredTestMethod().getName();
        String result = context.getExecutionException().isPresent() ? "failed" : "passed";
        writer.write("Test result: " + testName + " " + result + "\n");

        writer.close();
    }
}
