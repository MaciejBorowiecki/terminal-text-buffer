package terminal;

/**
 * Represents an immutable set of visual attributes for text.
 */
public record TextAttributes(
    byte foregroundColor,
    byte backgroundColor,
    boolean isBold,
    boolean isItalic,
    boolean isUnderline
) {
    // assuming ANSI codes for colors
    public static final byte COLOR_BLACK = 0;
    public static final byte COLOR_WHITE = 7;

    public TextAttributes() {
        this(COLOR_BLACK, COLOR_WHITE, false, false, false);
    }
}
