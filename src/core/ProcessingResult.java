package wordeditor.core;

/**
 * Result class for text processing operations
 */
public class ProcessingResult {
    private final String originalText;
    private final String processedText;
    private final SpellCheckResult spellCheckResult;
    private final long processingTimeMs;

    public ProcessingResult(String originalText, String processedText, SpellCheckResult spellCheckResult) {
        this.originalText = originalText;
        this.processedText = processedText;
        this.spellCheckResult = spellCheckResult;
        this.processingTimeMs = System.currentTimeMillis();
    }

    public String getOriginalText() {
        return originalText;
    }

    public String getProcessedText() {
        return processedText;
    }

    public SpellCheckResult getSpellCheckResult() {
        return spellCheckResult;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public boolean wasModified() {
        return !originalText.equals(processedText);
    }
}
