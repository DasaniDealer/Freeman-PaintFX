package com.paintfx.freemanpaintfx;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class PaintLogger {
    private static final PaintLogger instance = new PaintLogger();
    private final BlockingQueue<String> logQueue = new LinkedBlockingQueue<>();
    private final ExecutorService execute = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "PaintFX-Log-Thread");
        thread.setDaemon(true);
        return thread;
    });

    private final File logFile = new File("paint_history.log");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private PaintLogger() {
        execute.submit(this::processQueue);
    }

    public static PaintLogger getInstance() {
        return instance;
    }

    public void logHandle(String context, String actionDescription) {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(dateFormatter);
        String timeStr = now.format(timeFormatter);

        String logLine = String.format("%s [%s] %s %s%n", dateStr, context, timeStr, actionDescription);
        logQueue.offer(logLine);
    }

    private void processQueue() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String line = logQueue.take();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                    writer.write(line);
                }
                catch (IOException e) {
                    System.err.println("Failed to write to log file: " + e.getMessage());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void shutdown() {
        execute.shutdownNow();
    }
}

