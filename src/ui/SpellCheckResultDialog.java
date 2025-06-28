package wordeditor.ui;

import wordeditor.core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.List;

/**
 * Dialog for displaying spell check results
 */
public class SpellCheckResultDialog extends JDialog {
    private ProcessingResult result;
    private JList<String> errorsList;
    private JTextArea suggestionsArea;

    public SpellCheckResultDialog(Frame parent, ProcessingResult result) {
        super(parent, "Spell Check Results", true);
        this.result = result;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateResults();

        setSize(500, 400);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        errorsList = new JList<>();
        errorsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        suggestionsArea = new JTextArea(5, 20);
        suggestionsArea.setEditable(false);
        suggestionsArea.setBorder(BorderFactory.createTitledBorder("Suggestions"));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with results summary
        JPanel summaryPanel = new JPanel(new FlowLayout());
        SpellCheckResult spellResult = result.getSpellCheckResult();
        summaryPanel.add(new JLabel("Errors found: " + spellResult.getErrors().size()));
        summaryPanel.add(new JLabel("Correct words: " + spellResult.getCorrectWords().size()));
        add(summaryPanel, BorderLayout.NORTH);

        // Center panel with errors list
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Spelling Errors"));
        centerPanel.add(new JScrollPane(errorsList), BorderLayout.CENTER);
        centerPanel.add(new JScrollPane(suggestionsArea), BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEventHandlers() {
        errorsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selectedError = errorsList.getSelectedValue();
                if (selectedError != null) {
                    showSuggestionsForError(selectedError);
                }
            }
        });
    }

    private void populateResults() {
        SpellCheckResult spellResult = result.getSpellCheckResult();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String errorWord : spellResult.getErrors().keySet()) {
            listModel.addElement(errorWord);
        }
        errorsList.setModel(listModel);

        // Select first item if available
        if (listModel.getSize() > 0) {
            errorsList.setSelectedIndex(0);
        }
    }

    private void showSuggestionsForError(String errorWord) {
        SpellCheckResult spellResult = result.getSpellCheckResult();
        List<String> suggestions = spellResult.getErrors().get(errorWord);

        if (suggestions != null && !suggestions.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Suggestions for '").append(errorWord).append("':\n\n");
            for (int i = 0; i < suggestions.size(); i++) {
                sb.append(i + 1).append(". ").append(suggestions.get(i)).append("\n");
            }
            suggestionsArea.setText(sb.toString());
        } else {
            suggestionsArea.setText("No suggestions available for '" + errorWord + "'");
        }
    }
}
