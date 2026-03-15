package terminal;

/**
 * Represents a single line (horizontal line of text) on the terminal screen.
 * Stores characters (their Unicode) and their attributes (background/text colors, styles) in parallel arrays.
 * -1 represents that this cell is part of bigger character (occupying two cells).
 */

public class Row implements ReadableRow{
    private int width;
    private int[] characters;
    private byte[] bgColors;
    private byte[] fgColors;
    private boolean[] stylesItalic;
    private boolean[] stylesUnderline;
    private boolean[] stylesBold;

    public Row(int width) {
        this.width = width;
        characters = new int[width];
        bgColors = new byte[width];
        fgColors = new byte[width];
        stylesItalic = new boolean[width];
        stylesUnderline = new boolean[width];
        stylesBold = new boolean[width];

        java.util.Arrays.fill(characters, ' ');
    }

    public void setCell(int i, int uniCode, TextAttributes attributes){
        setCharacterUnicode(i, uniCode);
        setBackgroundColor(i, attributes.backgroundColor());
        setForegroundColor(i, attributes.foregroundColor());
        setItalic(i, attributes.isItalic());
        setBold(i, attributes.isBold());
        setUnderline(i, attributes.isUnderline());
    }

    public int getWidth(){
        return width;
    }

    public int getCharacterUnicode(int i){
        return characters[i];
    }

    public void setCharacterUnicode(int i, int uniCode){
        // The cell we are modifying was part of wider character
        if (i > 0 && characters[i] == -1){
            characters[i-1] = ' ';
        }
        if(i + 1 < getWidth() && characters[i+1] == -1){
            characters[i+1] = ' ';
        }
        if (WcWidth.calculateWidth(uniCode) == 2) {
            characters[i + 1] = -1; // The character after as will be part of us (wider character)
        }
        characters[i] = uniCode;
    }

    public void setForegroundColor(int i, byte c){
        fgColors[i] = c;
    }

    public void setBackgroundColor(int i, byte c){
        bgColors[i] = c;
    }

    public void setUnderline(int i, boolean b){
        stylesUnderline[i] = b;
    }

    public void setItalic(int i, boolean b){
        stylesItalic[i] = b;
    }

    public void setBold(int i, boolean b){
        stylesBold[i] = b;
    }

    public TextAttributes getTextAttributes(int i){
        return new TextAttributes(
                fgColors[i],
                bgColors[i],
                stylesBold[i],
                stylesItalic[i],
                stylesUnderline[i]
        );
    }

    /**
     * Fill row with default values.
     */
    public void clear() {
        java.util.Arrays.fill(characters, ' ');
        java.util.Arrays.fill(bgColors, (byte) 7); // Domyślny background (np. WHITE)
        java.util.Arrays.fill(fgColors, (byte) 0); // Domyślny foreground (np. BLACK)
        java.util.Arrays.fill(stylesBold, false);
        java.util.Arrays.fill(stylesItalic, false);
        java.util.Arrays.fill(stylesUnderline, false);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < width; i++) {
            int codePoint = characters[i];
            if(codePoint == -1){
                continue;
            }
            sb.append(Character.toChars(codePoint));
        }
        return sb.toString();
    }
}
