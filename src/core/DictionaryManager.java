package wordeditor.core;

import wordeditor.utils.Search;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.nio.file.*;

/**
 * Dictionary management with concurrent loading and caching
 * Demonstrates file I/O and thread-safe operations
 */
public class DictionaryManager {
    private static final String DEFAULT_DICTIONARY = "resources/dictionary.txt";
    private static final String FALLBACK_DICTIONARY = "resources/basic_words.txt";

    private final Set<String> words = ConcurrentHashMap.newKeySet();
    private volatile boolean loaded = false;
    private final Object loadLock = new Object();

    public DictionaryManager() {
        // Constructor doesn't load automatically - call loadDictionary() explicitly
    }

    /**
     * Load dictionary from file (thread-safe)
     */
    public void loadDictionary() {
        if (loaded)
            return;

        synchronized (loadLock) {
            if (loaded)
                return; // Double-check

            try {
                loadFromFile(getClass().getClassLoader().getResourceAsStream("dictionary.txt"));
            } catch (Exception e) {
                System.out.println("Loading fallback dictionary...");
                loadFallbackDictionary();
            }

            loaded = true;
            System.out.println("Dictionary loaded with " + words.size() + " words");
        }
    }

    private void loadFromFile(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new FileNotFoundException("Dictionary file not found");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim().toLowerCase();
                if (!word.isEmpty() && word.matches("[a-zA-Z]+")) {
                    words.add(word);
                }
            }
        }
    }

    private void loadFallbackDictionary() {
        // Basic English words for testing
        String[] basicWords = {
                "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
                "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
                "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
                "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
                "up", "out", "if", "about", "who", "get", "which", "go", "me", "when",
                "make", "can", "like", "time", "no", "just", "him", "know", "take", "people",
                "into", "year", "your", "good", "some", "could", "them", "see", "other", "than",
                "then", "now", "look", "only", "come", "its", "over", "think", "also", "back",
                "after", "use", "two", "how", "our", "work", "first", "well", "way", "even",
                "new", "want", "because", "any", "these", "give", "day", "most", "us", "is",
                "was", "are", "been", "has", "had", "were", "said", "each", "which", "their",
                "time", "will", "about", "if", "up", "out", "many", "then", "them", "write",
                "would", "like", "so", "these", "her", "long", "make", "thing", "see", "him",
                "two", "more", "go", "no", "way", "could", "my", "than", "first", "water",
                "been", "call", "who", "oil", "sit", "now", "find", "down", "day", "did",
                "get", "has", "may", "part", "over", "new", "sound", "take", "only", "little",
                "work", "know", "place", "year", "live", "me", "back", "give", "most", "very",
                "after", "thing", "our", "just", "name", "good", "sentence", "man", "think", "say",
                "great", "where", "help", "through", "much", "before", "line", "right", "too", "mean",
                "old", "any", "same", "tell", "boy", "follow", "came", "want", "show", "also",
                "around", "form", "three", "small", "set", "put", "end", "why", "again", "turn",
                "here", "off", "went", "old", "number", "great", "tell", "men", "say", "small",
                "every", "found", "still", "between", "mane", "should", "home", "big", "give", "air",
                "line", "set", "own", "under", "read", "last", "never", "us", "left", "end",
                "along", "while", "might", "next", "sound", "below", "saw", "something", "thought", "both",
                "few", "those", "always", "looked", "show", "large", "often", "together", "asked", "house",
                "don't", "world", "going", "want", "school", "important", "until", "form", "food", "keep",
                "children", "feet", "land", "side", "without", "boy", "once", "animal", "life", "enough",
                "took", "sometimes", "four", "head", "above", "kind", "began", "almost", "live", "page",
                "got", "earth", "need", "far", "hand", "high", "year", "mother", "light", "country",
                "father", "let", "night", "picture", "being", "study", "second", "book", "carry", "science",
                "eat", "room", "friend", "began", "idea", "fish", "mountain", "north", "once", "base",
                "hear", "horse", "cut", "sure", "watch", "color", "face", "wood", "main", "enough",
                "plain", "girl", "usual", "young", "ready", "above", "ever", "red", "list", "though",
                "feel", "talk", "bird", "soon", "body", "dog", "family", "direct", "leave", "song",
                "measure", "door", "product", "black", "short", "numeral", "class", "wind", "question", "happen",
                "complete", "ship", "area", "half", "rock", "order", "fire", "south", "problem", "piece",
                "told", "knew", "pass", "since", "top", "whole", "king", "space", "heard", "best",
                "hour", "better", "during", "hundred", "five", "remember", "step", "early", "hold", "west",
                "ground", "interest", "reach", "fast", "verb", "sing", "listen", "six", "table", "travel",
                "less", "morning", "ten", "simple", "several", "vowel", "toward", "war", "lay", "against",
                "pattern", "slow", "center", "love", "person", "money", "serve", "appear", "road", "map",
                "rain", "rule", "govern", "pull", "cold", "notice", "voice", "unit", "power", "town",
                "fine", "certain", "fly", "fall", "lead", "cry", "dark", "machine", "note", "wait",
                "plan", "figure", "star", "box", "noun", "field", "rest", "correct", "able", "pound",
                "done", "beauty", "drive", "stood", "contain", "front", "teach", "week", "final", "gave",
                "green", "oh", "quick", "develop", "ocean", "warm", "free", "minute", "strong", "special",
                "mind", "behind", "clear", "tail", "produce", "fact", "street", "inch", "multiply", "nothing",
                "course", "stay", "wheel", "full", "force", "blue", "object", "decide", "surface", "deep",
                "moon", "island", "foot", "system", "busy", "test", "record", "boat", "common", "gold",
                "possible", "plane", "stead", "dry", "wonder", "laugh", "thousands", "ago", "ran", "check",
                "game", "shape", "equate", "miss", "brought", "heat", "snow", "tire", "bring", "yes",
                "distant", "fill", "east", "paint", "language", "among", "grand", "ball", "yet", "wave",
                "drop", "heart", "am", "present", "heavy", "dance", "engine", "position", "arm", "wide",
                "sail", "material", "size", "vary", "settle", "speak", "weight", "general", "ice", "matter",
                "circle", "pair", "include", "divide", "syllable", "felt", "perhaps", "pick", "sudden", "count",
                "square", "reason", "length", "represent", "art", "subject", "region", "energy", "hunt", "probable",
                "bed", "brother", "egg", "ride", "cell", "believe", "fraction", "forest", "sit", "race",
                "window", "store", "summer", "train", "sleep", "prove", "lone", "leg", "exercise", "wall",
                "catch", "mount", "wish", "sky", "board", "joy", "winter", "sat", "written", "wild",
                "instrument", "kept", "glass", "grass", "cow", "job", "edge", "sign", "visit", "past",
                "soft", "fun", "bright", "gas", "weather", "month", "million", "bear", "finish", "happy",
                "hope", "flower", "clothe", "strange", "gone", "jump", "baby", "eight", "village", "meet",
                "root", "buy", "raise", "solve", "metal", "whether", "push", "seven", "paragraph", "third",
                "shall", "held", "hair", "describe", "cook", "floor", "either", "result", "burn", "hill",
                "safe", "cat", "century", "consider", "type", "law", "bit", "coast", "copy", "phrase",
                "silent", "tall", "sand", "soil", "roll", "temperature", "finger", "industry", "value", "fight",
                "lie", "beat", "excite", "natural", "view", "sense", "ear", "else", "quite", "broke",
                "case", "middle", "kill", "son", "lake", "moment", "scale", "loud", "spring", "observe",
                "child", "straight", "consonant", "nation", "dictionary", "milk", "speed", "method", "organ", "pay",
                "age", "section", "dress", "cloud", "surprise", "quiet", "stone", "tiny", "climb", "bad",
                "oil", "blood", "touch", "grew", "cent", "mix", "team", "wire", "cost", "lost",
                "brown", "wear", "garden", "equal", "sent", "choose", "fell", "fit", "flow", "fair",
                "bank", "collect", "save", "control", "decimal", "gentle", "woman", "captain", "practice", "separate",
                "difficult", "doctor", "please", "protect", "noon", "whose", "locate", "ring", "character", "insect",
                "caught", "period", "indicate", "radio", "spoke", "atom", "human", "history", "effect", "electric",
                "expect", "crop", "modern", "element", "hit", "student", "corner", "party", "supply", "bone",
                "rail", "imagine", "provide", "agree", "thus", "capital", "won't", "chair", "danger", "fruit",
                "rich", "thick", "soldier", "process", "operate", "guess", "necessary", "sharp", "wing", "create",
                "neighbor", "wash", "bat", "rather", "crowd", "corn", "compare", "poem", "string", "bell",
                "depend", "meat", "rub", "tube", "famous", "dollar", "stream", "fear", "sight", "thin",
                "triangle", "planet", "hurry", "chief", "colony", "clock", "mine", "tie", "enter", "major",
                "fresh", "search", "send", "yellow", "gun", "allow", "print", "dead", "spot", "desert",
                "suit", "current", "lift", "rose", "continue", "block", "chart", "hat", "sell", "success",
                "company", "subtract", "event", "particular", "deal", "swim", "term", "opposite", "wife", "shoe",
                "shoulder", "spread", "arrange", "camp", "invent", "cotton", "born", "determine", "quart", "nine",
                "truck", "noise", "level", "chance", "gather", "shop", "stretch", "throw", "shine", "property",
                "column", "molecule", "select", "wrong", "gray", "repeat", "require", "broad", "prepare", "salt",
                "nose", "plural", "anger", "claim", "continent", "oxygen", "sugar", "death", "pretty", "skill",
                "women", "season", "solution", "magnet", "silver", "thank", "branch", "match", "suffix", "especially",
                "fig", "afraid", "huge", "sister", "steel", "discuss", "forward", "similar", "guide", "experience",
                "score", "apple", "bought", "led", "pitch", "coat", "mass", "card", "band", "rope",
                "slip", "win", "dream", "evening", "condition", "feed", "tool", "total", "basic", "smell",
                "valley", "nor", "double", "seat", "arrive", "master", "track", "parent", "shore", "division",
                "sheet", "substance", "favor", "connect", "post", "spend", "chord", "fat", "glad", "original",
                "share", "station", "dad", "bread", "charge", "proper", "bar", "offer", "segment", "slave",
                "duck", "instant", "market", "degree", "populate", "chick", "dear", "enemy", "reply", "drink",
                "occur", "support", "speech", "nature", "range", "steam", "motion", "path", "liquid", "log",
                "meant", "quotient", "teeth", "shell", "neck"
        };

        Collections.addAll(words, basicWords);
    }

    /**
     * Check if word exists in dictionary (thread-safe)
     */
    public boolean contains(String word) {
        return words.contains(word.toLowerCase());
    }

    /**
     * Get all words starting with prefix
     */
    public List<String> getWordsStartingWith(String prefix) {
        String lowerPrefix = prefix.toLowerCase();
        return words.stream()
                .filter(word -> word.startsWith(lowerPrefix))
                .sorted()
                .limit(10) // Limit suggestions
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * Get dictionary size
     */
    public int size() {
        return words.size();
    }

    /**
     * Check if dictionary is loaded
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Add custom word to dictionary
     */
    public void addWord(String word) {
        words.add(word.toLowerCase());
    }

    /**
     * Get all words as array for binary search
     */
    public String[] getWordsArray() {
        return words.toArray(new String[0]);
    }
}
