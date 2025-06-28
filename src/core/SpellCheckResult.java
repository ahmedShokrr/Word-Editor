package wordeditor.core;

import java.util.*;

/**
 * Result class for spell checking operations
 */
public class SpellCheckResult {
    private final Map<String, List<String>> errors;
    private final Set<String> correctWords;
    private final boolean hasErrors;

    public SpellCheckResult() {
        this.errors = new HashMap<>();
        this.correctWords = new HashSet<>();
        this.hasErrors = false;
    }

    public SpellCheckResult(Map<String, List<String>> errors, Set<String> correctWords) {
        this.errors = new HashMap<>(errors);
        this.correctWords = new HashSet<>(correctWords);
        this.hasErrors = !errors.isEmpty();
    }

    public Map<String, List<String>> getErrors() {
        return new HashMap<>(errors);
    }

    public Set<String> getCorrectWords() {
        return new HashSet<>(correctWords);
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public void addError(String word, List<String> suggestions) {
        errors.put(word, new ArrayList<>(suggestions));
    }

    public void addCorrectWord(String word) {
        correctWords.add(word);
    }
}
