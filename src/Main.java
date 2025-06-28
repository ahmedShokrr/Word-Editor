package wordeditor;

import wordeditor.ui.WordEditorGUI;
import wordeditor.core.WordProcessor;

/**
 * Main entry point for the Word Editor application
 * Demonstrates multithreading concepts for Operating Systems course
 */
public class Main {
    public static void main(String[] args) {
        // Launch GUI version
        if (args.length > 0 && args[0].equals("--console")) {
            // Console version for demonstration
            WordProcessor processor = new WordProcessor();
            processor.runConsoleMode();
        } else {
            // Launch GUI version
            javax.swing.SwingUtilities.invokeLater(() -> {
                new WordEditorGUI().setVisible(true);
            });
        }
    }
}
