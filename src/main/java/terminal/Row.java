package terminal;

import java.util.Arrays;

/**
 * Represents a single line (horizontal line of text) on the terminal screen.
 * Stores characters (their Unicode) and their attributes (background/text colors, styles) in parallel arrays.
 * -1 represents that this cell is part of bigger character (occupying two cells).
 */

public class Row implements ReadableRow{
    private final int width;
    private final int[] characters;
    private final byte[] bgColors;
    private final byte[] fgColors;
    private final boolean[] stylesItalic;
    private final boolean[] stylesUnderline;
    private final boolean[] stylesBold;
    private boolean isWrapped = false;

    private static final int WIDER_DUMMY = -1;
    private static final int DEFAULT_CHARACTER = ' ';
    private static final int WIDER_CHARACTER = 2;

    public Row(int width) {
        this.width = width;
        characters = new int[width];
        bgColors = new byte[width];
        fgColors = new byte[width];
        stylesItalic = new boolean[width];
        stylesUnderline = new boolean[width];
        stylesBold = new boolean[width];

        Arrays.fill(characters, DEFAULT_CHARACTER);
    }

    public void setCell(int i, int uniCode, TextAttributes attributes){
        setCharacterUnicode(i, uniCode);
        setBackgroundColor(i, attributes.backgroundColor());
        setForegroundColor(i, attributes.foregroundColor());
        setItalic(i, attributes.isItalic());
        setBold(i, attributes.isBold());
        setUnderline(i, attributes.isUnderline());
    }

    /**
     * Optimized method for positioning an entire cell using only primitives.
     */
    public void setCell(int i, int codePoint, byte fg, byte bg, boolean bold,
                        boolean italic, boolean underline) {
        setCharacterUnicode(i, codePoint);
        setForegroundColor(i, fg);
        setBackgroundColor(i, bg);
        setBold(i, bold);
        setItalic(i, italic);
        setUnderline(i, underline);
    }

    private void checkBounds(int i) {
        if (i < 0 || i >= width) {
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean isWrapped() {
        return isWrapped;
    }
    public void setWrapped(boolean isWrapped) {
        this.isWrapped = isWrapped;
    }

    public int getWidth(){
        return width;
    }

    public int getCharacterUnicode(int i){
        checkBounds(i);
        return characters[i];
    }

    public void setCharacterUnicode(int i, int uniCode){
        checkBounds(i);
        // The cell we are modifying was part of wider character
        if (i > 0 && characters[i] == WIDER_DUMMY) {
            clearCell(i - 1);
        }
        if(i + 1 < getWidth() && characters[i+1] == WIDER_DUMMY){
            clearCell(i + 1);
        }
        if (WcWidth.calculateWidth(uniCode) == WIDER_CHARACTER) {
            characters[i + 1] = WIDER_DUMMY; // The character after as will be part of us (wider character)
        }
        characters[i] = uniCode;
    }

    public void setForegroundColor(int i, byte c){
        checkBounds(i);
        fgColors[i] = c;
    }

    public void setBackgroundColor(int i, byte c){
        checkBounds(i);
        bgColors[i] = c;
    }

    public void setUnderline(int i, boolean b){
        checkBounds(i);
        stylesUnderline[i] = b;
    }

    public void setItalic(int i, boolean b){
        checkBounds(i);
        stylesItalic[i] = b;
    }

    public void setBold(int i, boolean b){
        checkBounds(i);
        stylesBold[i] = b;
    }

    public TextAttributes getTextAttributes(int i){
        checkBounds(i);
        return new TextAttributes(
                fgColors[i],
                bgColors[i],
                stylesBold[i],
                stylesItalic[i],
                stylesUnderline[i]
        );
    }

    public byte getForegroundColorAt(int i) {
        checkBounds(i);
        return fgColors[i];
    }

    public byte getBackgroundColorAt(int i) {
        checkBounds(i);
        return bgColors[i];
    }

    public boolean isBoldAt(int i) {
        checkBounds(i);
        return stylesBold[i];
    }

    public boolean isItalicAt(int i) {
        checkBounds(i);
        return stylesItalic[i];
    }

    public boolean isUnderlineAt(int i) {
        checkBounds(i);
        return stylesUnderline[i];
    }

    /**
     * Fill row with default values.
     */
    public void clear() {
        java.util.Arrays.fill(characters, DEFAULT_CHARACTER);
        java.util.Arrays.fill(bgColors, TextAttributes.COLOR_BLACK);
        java.util.Arrays.fill(fgColors, TextAttributes.COLOR_WHITE);
        java.util.Arrays.fill(stylesBold, false);
        java.util.Arrays.fill(stylesItalic, false);
        java.util.Arrays.fill(stylesUnderline, false);
        setWrapped(false);
    }

    public void clearCell(int i){
        checkBounds(i);
        characters[i] = DEFAULT_CHARACTER;
        bgColors[i] = TextAttributes.COLOR_BLACK;
        fgColors[i] = TextAttributes.COLOR_WHITE;
        stylesItalic[i] = false;
        stylesUnderline[i] = false;
        stylesBold[i] = false;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) {
            int codePoint = characters[i];
            if(codePoint == WIDER_DUMMY){
                continue;
            }
            sb.append(Character.toChars(codePoint));
        }
        return sb.toString();
    }
}
