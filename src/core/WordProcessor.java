package wordeditor.core;

import wordeditor.utils.Stack;
import wordeditor.utils.Search;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;

/**
 * Core word processing engine with multithreading capabilities
 * Demonstrates producer-consumer pattern and thread synchronization
 */
public class WordProcessor {
    private final Object textLock = new Object();
    private final BlockingQueue<String> textQueue = new LinkedBlockingQueue<>();
    private final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    private volatile String currentText = "";
    private volatile boolean processingComplete = false;

    private DictionaryManager dictionary;
    private SpellChecker spellChecker;
    private AutoCorrector autoCorrector;

    public WordProcessor() {
        this.dictionary = new DictionaryManager();
        this.spellChecker = new SpellChecker(dictionary);
        this.autoCorrector = new AutoCorrector(dictionary);

        // Initialize dictionary in background
        CompletableFuture.runAsync(() -> dictionary.loadDictionary());
    }

    /**
     * Process text using multiple threads
     */
    public CompletableFuture<ProcessingResult> processText(String inputText) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Stage 1: Text preprocessing (Thread 1)
                String cleanedText = preprocessText(inputText);

                // Stage 2: Spell checking (Thread 2)
                SpellCheckResult spellResult = spellChecker.checkText(cleanedText);

                // Stage 3: Auto-correction (Thread 3)
                String correctedText = autoCorrector.correctText(cleanedText, spellResult);

                // Stage 4: Post-processing (Thread 4)
                String finalText = postprocessText(correctedText);

                return new ProcessingResult(inputText, finalText, spellResult);

            } catch (Exception e) {
                throw new RuntimeException("Text processing failed", e);
            }
        }, threadPool);
    }

    /**
     * Console mode for demonstration
     */
    public void runConsoleMode() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Word Editor - Console Mode ===");
        System.out.println("Type 'exit' to quit");

        while (true) {
            System.out.print("\nEnter text to process: ");
            String input = scanner.nextLine();

            if ("exit".equalsIgnoreCase(input.trim())) {
                break;
            }

            if (input.trim().isEmpty()) {
                continue;
            }

            try {
                ProcessingResult result = processText(input).get();
                displayResult(result);
            } catch (Exception e) {
                System.err.println("Error processing text: " + e.getMessage());
            }
        }

        shutdown();
        scanner.close();
    }

    private void displayResult(ProcessingResult result) {
        System.out.println("\n=== Processing Result ===");
        System.out.println("Original: " + result.getOriginalText());
        System.out.println("Processed: " + result.getProcessedText());

        SpellCheckResult spellResult = result.getSpellCheckResult();
        if (spellResult.hasErrors()) {
            System.out.println("\nSpelling Issues Found:");
            spellResult.getErrors().forEach((word, suggestions) -> {
                System.out.println("- '" + word + "' -> Suggestions: " +
                        String.join(", ", suggestions));
            });
        } else {
            System.out.println("\nNo spelling errors found!");
        }
    }

    private String preprocessText(String text) {
        // Remove extra spaces, normalize punctuation
        return text.trim().replaceAll("\\s+", " ");
    }

    private String postprocessText(String text) {
        // Final formatting
        return text.trim();
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Getters for GUI integration
    public DictionaryManager getDictionary() {
        return dictionary;
    }

    public SpellChecker getSpellChecker() {
        return spellChecker;
    }

    public AutoCorrector getAutoCorrector() {
        return autoCorrector;
    }
}
