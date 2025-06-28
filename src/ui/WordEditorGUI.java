package wordeditor.ui;

import wordeditor.core.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Map;

/**
 * Modern GUI for the Word Editor
 * Demonstrates real-time spell checking and auto-correction
 */
public class WordEditorGUI extends JFrame {
    private JTextPane textPane;
    private JLabel statusLabel;
    private JPanel suggestionsPanel;
    private JProgressBar progressBar;

    private WordProcessor processor;
    private Timer spellCheckTimer;

    // Colors for highlighting
    private static final Color ERROR_COLOR = new Color(255, 200, 200);
    private static final Color CORRECT_COLOR = new Color(200, 255, 200);

    public WordEditorGUI() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        processor = new WordProcessor();
        setupSpellCheckTimer();
    }

    private void initializeComponents() {
        setTitle("Word Editor - Operating Systems Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Text area with styling
        textPane = new JTextPane();
        textPane.setFont(new Font("Arial", Font.PLAIN, 14));
        textPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Status bar
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setVisible(false);

        // Suggestions panel
        suggestionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        suggestionsPanel.setBorder(BorderFactory.createTitledBorder("Suggestions"));
        suggestionsPanel.setPreferredSize(new Dimension(0, 80));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Main text area
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(suggestionsPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(progressBar, BorderLayout.EAST);
        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Toolbar
        JToolBar toolBar = createToolBar();
        add(toolBar, BorderLayout.NORTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.add(createMenuItem("New", e -> newDocument()));
        fileMenu.add(createMenuItem("Open", e -> openDocument()));
        fileMenu.add(createMenuItem("Save", e -> saveDocument()));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", e -> System.exit(0)));

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        editMenu.add(createMenuItem("Cut", e -> textPane.cut()));
        editMenu.add(createMenuItem("Copy", e -> textPane.copy()));
        editMenu.add(createMenuItem("Paste", e -> textPane.paste()));
        editMenu.addSeparator();
        editMenu.add(createMenuItem("Select All", e -> textPane.selectAll()));

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.add(createMenuItem("Check Spelling", e -> performSpellCheck()));
        toolsMenu.add(createMenuItem("Auto-Correct", e -> performAutoCorrect()));
        toolsMenu.addSeparator();

        JCheckBoxMenuItem autoCheckItem = new JCheckBoxMenuItem("Auto Spell Check", true);
        autoCheckItem.addActionListener(e -> toggleAutoSpellCheck(autoCheckItem.isSelected()));
        toolsMenu.add(autoCheckItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(toolsMenu);

        return menuBar;
    }

    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(createButton("New", "📄", e -> newDocument()));
        toolBar.add(createButton("Open", "📂", e -> openDocument()));
        toolBar.add(createButton("Save", "💾", e -> saveDocument()));
        toolBar.addSeparator();

        toolBar.add(createButton("Spell Check", "✓", e -> performSpellCheck()));
        toolBar.add(createButton("Auto-Correct", "🔧", e -> performAutoCorrect()));

        return toolBar;
    }

    private JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem item = new JMenuItem(text);
        item.addActionListener(listener);
        return item;
    }

    private JButton createButton(String tooltip, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.addActionListener(listener);
        return button;
    }

    private void setupEventHandlers() {
        // Document change listener for real-time spell checking
        textPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                scheduleSpellCheck();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                scheduleSpellCheck();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                scheduleSpellCheck();
            }
        });

        // Mouse listener for word selection
        textPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    selectWordAtCursor();
                    showSuggestionsForSelectedWord();
                }
            }
        });
    }

    private void setupSpellCheckTimer() {
        spellCheckTimer = new Timer(1000, e -> performRealtimeSpellCheck());
        spellCheckTimer.setRepeats(false);
    }

    private void scheduleSpellCheck() {
        spellCheckTimer.restart();
    }

    private void performRealtimeSpellCheck() {
        String text = textPane.getText();
        if (text.trim().isEmpty())
            return;

        CompletableFuture.supplyAsync(() -> processor.processText(text))
                .thenAccept(result -> SwingUtilities.invokeLater(() -> {
                    try {
                        ProcessingResult processingResult = result.get();
                        highlightErrors(processingResult.getSpellCheckResult());
                        updateStatus("Real-time spell check complete");
                    } catch (Exception ex) {
                        updateStatus("Spell check error: " + ex.getMessage());
                    }
                }));
    }

    private void performSpellCheck() {
        String text = textPane.getText();
        if (text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some text first.",
                    "No Text", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        showProgress(true);
        updateStatus("Checking spelling...");

        processor.processText(text)
                .thenAccept(result -> SwingUtilities.invokeLater(() -> {
                    showProgress(false);
                    displaySpellCheckResults(result);
                }))
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> {
                        showProgress(false);
                        updateStatus("Error: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    private void performAutoCorrect() {
        String text = textPane.getText();
        if (text.trim().isEmpty())
            return;

        showProgress(true);
        updateStatus("Auto-correcting...");

        processor.processText(text)
                .thenAccept(result -> SwingUtilities.invokeLater(() -> {
                    showProgress(false);
                    if (result.wasModified()) {
                        textPane.setText(result.getProcessedText());
                        updateStatus("Auto-correction applied");
                    } else {
                        updateStatus("No corrections needed");
                    }
                }))
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> {
                        showProgress(false);
                        updateStatus("Auto-correct error: " + throwable.getMessage());
                    });
                    return null;
                });
    }

    private void highlightErrors(SpellCheckResult result) {
        StyledDocument doc = textPane.getStyledDocument();

        // Clear existing styles
        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setBackground(normal, Color.WHITE);
        doc.setCharacterAttributes(0, doc.getLength(), normal, true);

        // Highlight errors
        String text = textPane.getText().toLowerCase();
        SimpleAttributeSet errorStyle = new SimpleAttributeSet();
        StyleConstants.setBackground(errorStyle, ERROR_COLOR);

        for (String errorWord : result.getErrors().keySet()) {
            int index = 0;
            while ((index = text.indexOf(errorWord, index)) != -1) {
                doc.setCharacterAttributes(index, errorWord.length(), errorStyle, false);
                index += errorWord.length();
            }
        }
    }

    private void displaySpellCheckResults(ProcessingResult result) {
        SpellCheckResult spellResult = result.getSpellCheckResult();

        if (!spellResult.hasErrors()) {
            JOptionPane.showMessageDialog(this,
                    "No spelling errors found!",
                    "Spell Check Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            updateStatus("No errors found");
        } else {
            showSpellCheckDialog(result);
            updateStatus(spellResult.getErrors().size() + " errors found");
        }
    }

    private void showSpellCheckDialog(ProcessingResult result) {
        SpellCheckResultDialog dialog = new SpellCheckResultDialog(this, result);
        dialog.setVisible(true);
    }

    private void selectWordAtCursor() {
        int caretPos = textPane.getCaretPosition();
        String text = textPane.getText();

        int start = caretPos;
        int end = caretPos;

        // Find word boundaries
        while (start > 0 && Character.isLetter(text.charAt(start - 1))) {
            start--;
        }
        while (end < text.length() && Character.isLetter(text.charAt(end))) {
            end++;
        }

        if (start < end) {
            textPane.setSelectionStart(start);
            textPane.setSelectionEnd(end);
        }
    }

    private void showSuggestionsForSelectedWord() {
        String selectedText = textPane.getSelectedText();
        if (selectedText == null || selectedText.trim().isEmpty())
            return;

        suggestionsPanel.removeAll();

        List<String> suggestions = processor.getAutoCorrector()
                .getSuggestionsForWord(selectedText.trim());

        if (suggestions.isEmpty()) {
            suggestionsPanel.add(new JLabel("No suggestions available"));
        } else {
            for (String suggestion : suggestions) {
                JButton suggestionButton = new JButton(suggestion);
                suggestionButton.addActionListener(e -> replaceSelectedWord(suggestion));
                suggestionsPanel.add(suggestionButton);
            }
        }

        suggestionsPanel.revalidate();
        suggestionsPanel.repaint();
    }

    private void replaceSelectedWord(String replacement) {
        textPane.replaceSelection(replacement);
        suggestionsPanel.removeAll();
        suggestionsPanel.revalidate();
        suggestionsPanel.repaint();
    }

    private void toggleAutoSpellCheck(boolean enabled) {
        if (enabled) {
            spellCheckTimer.start();
            updateStatus("Auto spell check enabled");
        } else {
            spellCheckTimer.stop();
            updateStatus("Auto spell check disabled");
        }
    }

    private void showProgress(boolean show) {
        progressBar.setVisible(show);
        if (show) {
            progressBar.setIndeterminate(true);
        } else {
            progressBar.setIndeterminate(false);
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void newDocument() {
        if (confirmUnsavedChanges()) {
            textPane.setText("");
            suggestionsPanel.removeAll();
            suggestionsPanel.revalidate();
            updateStatus("New document created");
        }
    }

    private void openDocument() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Implementation for opening files
            updateStatus("Open functionality not implemented");
        }
    }

    private void saveDocument() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Implementation for saving files
            updateStatus("Save functionality not implemented");
        }
    }

    private boolean confirmUnsavedChanges() {
        // Simple implementation - in real app, check if document is modified
        return true;
    }

    @Override
    public void dispose() {
        if (processor != null) {
            processor.shutdown();
        }
        super.dispose();
    }
}
