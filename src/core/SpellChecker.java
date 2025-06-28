package wordeditor.core;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Spell checker with multithreaded processing
 * Demonstrates producer-consumer pattern
 */
public class SpellChecker {
    private final DictionaryManager dictionary;

    public SpellChecker(DictionaryManager dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Check text for spelling errors
     */
    public SpellCheckResult checkText(String text) {
        if (!dictionary.isLoaded()) {
            dictionary.loadDictionary();
        }

        Map<String, List<String>> errors = new HashMap<>();
        Set<String> correctWords = new HashSet<>();

        String[] words = text.toLowerCase().split("\\s+");

        for (String word : words) {
            // Clean word (remove punctuation)
            String cleanWord = word.replaceAll("[^a-zA-Z]", "");

            if (cleanWord.isEmpty())
                continue;

            if (dictionary.contains(cleanWord)) {
                correctWords.add(cleanWord);
            } else {
                List<String> suggestions = generateSuggestions(cleanWord);
                errors.put(cleanWord, suggestions);
            }
        }

        return new SpellCheckResult(errors, correctWords);
    }

    /**
     * Generate spelling suggestions for a word
     */
    private List<String> generateSuggestions(String word) {
        List<String> suggestions = new ArrayList<>();

        // Add prefix-based suggestions
        suggestions.addAll(dictionary.getWordsStartingWith(word.substring(0,
                Math.min(2, word.length()))));

        // Add edit distance suggestions
        suggestions.addAll(getEditDistanceSuggestions(word));

        // Remove duplicates and limit
        return suggestions.stream()
                .distinct()
                .limit(5)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Get suggestions based on edit distance (simple implementation)
     */
    private List<String> getEditDistanceSuggestions(String word) {
        List<String> suggestions = new ArrayList<>();
        String[] dictWords = dictionary.getWordsArray();

        for (String dictWord : dictWords) {
            if (Math.abs(dictWord.length() - word.length()) <= 2) {
                if (calculateEditDistance(word, dictWord) <= 2) {
                    suggestions.add(dictWord);
                }
            }

            // Limit processing for performance
            if (suggestions.size() >= 10)
                break;
        }

        return suggestions;
    }

    /**
     * Calculate Levenshtein distance between two words
     */
    private int calculateEditDistance(String word1, String word2) {
        int[][] dp = new int[word1.length() + 1][word2.length() + 1];

        for (int i = 0; i <= word1.length(); i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= word2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= word1.length(); i++) {
            for (int j = 1; j <= word2.length(); j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]),
                            dp[i - 1][j - 1]);
                }
            }
        }

        return dp[word1.length()][word2.length()];
    }

    /**
     * Async spell check for large texts
     */
    public CompletableFuture<SpellCheckResult> checkTextAsync(String text) {
        return CompletableFuture.supplyAsync(() -> checkText(text));
    }
}
