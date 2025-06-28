package wordeditor.core;

import wordeditor.utils.Stack;
import wordeditor.utils.SortUtils;
import java.util.*;

/**
 * Auto-correction engine with advanced suggestion algorithms
 * Demonstrates stack-based suggestion management
 */
public class AutoCorrector {
    private final DictionaryManager dictionary;
    private final Stack<String> suggestionStack;

    public AutoCorrector(DictionaryManager dictionary) {
        this.dictionary = dictionary;
        this.suggestionStack = new Stack<>(50);
    }

    /**
     * Correct text based on spell check results
     */
    public String correctText(String originalText, SpellCheckResult spellResult) {
        if (!spellResult.hasErrors()) {
            return originalText;
        }

        String correctedText = originalText;
        Map<String, List<String>> errors = spellResult.getErrors();

        for (Map.Entry<String, List<String>> entry : errors.entrySet()) {
            String incorrectWord = entry.getKey();
            List<String> suggestions = entry.getValue();

            if (!suggestions.isEmpty()) {
                // Use the first (best) suggestion
                String correction = suggestions.get(0);
                correctedText = correctedText.replaceAll("\\b" + incorrectWord + "\\b", correction);
            }
        }

        return correctedText;
    }

    /**
     * Get suggestions for a specific word using advanced algorithms
     */
    public List<String> getSuggestionsForWord(String word) {
        suggestionStack.clear();

        // Algorithm 1: Character anagram matching
        addAnagramSuggestions(word);

        // Algorithm 2: Phonetic similarity
        addPhoneticSuggestions(word);

        // Algorithm 3: Common typo patterns
        addTypoSuggestions(word);

        // Convert stack to list
        List<String> suggestions = new ArrayList<>();
        while (!suggestionStack.isEmpty()) {
            suggestions.add(suggestionStack.pop());
        }

        return suggestions;
    }

    /**
     * Find words that are anagrams or have similar character composition
     */
    private void addAnagramSuggestions(String word) {
        String[] dictWords = dictionary.getWordsArray();

        for (String dictWord : dictWords) {
            if (word.length() == dictWord.length() &&
                    containsAllChars(word, dictWord)) {
                suggestionStack.push(dictWord);
            }
        }
    }

    /**
     * Add suggestions based on phonetic similarity
     */
    private void addPhoneticSuggestions(String word) {
        // Simple phonetic matching - replace similar sounding letters
        String[] phoneticVariants = generatePhoneticVariants(word);

        for (String variant : phoneticVariants) {
            if (dictionary.contains(variant)) {
                suggestionStack.push(variant);
            }
        }
    }

    /**
     * Add suggestions based on common typing errors
     */
    private void addTypoSuggestions(String word) {
        Set<String> suggestions = new HashSet<>();

        // Transposition (swap adjacent characters)
        for (int i = 0; i < word.length() - 1; i++) {
            char[] chars = word.toCharArray();
            char temp = chars[i];
            chars[i] = chars[i + 1];
            chars[i + 1] = temp;
            String suggestion = new String(chars);
            if (dictionary.contains(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Insertion (add a character)
        for (int i = 0; i <= word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                String suggestion = word.substring(0, i) + c + word.substring(i);
                if (dictionary.contains(suggestion)) {
                    suggestions.add(suggestion);
                }
            }
        }

        // Deletion (remove a character)
        for (int i = 0; i < word.length(); i++) {
            String suggestion = word.substring(0, i) + word.substring(i + 1);
            if (dictionary.contains(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        // Substitution (replace a character)
        for (int i = 0; i < word.length(); i++) {
            for (char c = 'a'; c <= 'z'; c++) {
                if (c != word.charAt(i)) {
                    char[] chars = word.toCharArray();
                    chars[i] = c;
                    String suggestion = new String(chars);
                    if (dictionary.contains(suggestion)) {
                        suggestions.add(suggestion);
                    }
                }
            }
        }

        // Add to stack (limit to prevent overflow)
        suggestions.stream().limit(10).forEach(suggestionStack::push);
    }

    /**
     * Generate phonetic variants of a word
     */
    private String[] generatePhoneticVariants(String word) {
        List<String> variants = new ArrayList<>();

        // Common phonetic substitutions
        variants.add(word.replace("ph", "f"));
        variants.add(word.replace("f", "ph"));
        variants.add(word.replace("c", "k"));
        variants.add(word.replace("k", "c"));
        variants.add(word.replace("s", "z"));
        variants.add(word.replace("z", "s"));

        return variants.toArray(new String[0]);
    }

    /**
     * Check if two words contain all the same characters (anagram check)
     */
    public boolean containsAllChars(String word1, String word2) {
        if (word1.length() != word2.length()) {
            return false;
        }

        Character[] chars1 = stringToCharArray(word1);
        Character[] chars2 = stringToCharArray(word2);

        SortUtils.insertionSort(chars1);
        SortUtils.insertionSort(chars2);

        return Arrays.equals(chars1, chars2);
    }

    /**
     * Convert string to Character array for sorting
     */
    private Character[] stringToCharArray(String str) {
        Character[] charArray = new Character[str.length()];
        for (int i = 0; i < str.length(); i++) {
            charArray[i] = str.charAt(i);
        }
        return charArray;
    }
}
