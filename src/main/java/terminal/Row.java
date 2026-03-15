package terminal;

/**
 * Represents a single line (horizontal line of text) on the terminal screen.
 * Stores characters and their attributes (background/text colors, styles) in parallel arrays.
 */

public class Row implements ReadableRow{
    private int width;
    private char[] characters;
    private byte[] bgColors;
    private byte[] fgColors;
    private boolean[] stylesItalic;
    private boolean[] stylesUnderline;
    private boolean[] stylesBold;

    public Row(int width) {
        this.width = width;
        characters = new char[width];
        bgColors = new byte[width];
        fgColors = new byte[width];
        stylesItalic = new boolean[width];
        stylesUnderline = new boolean[width];
        stylesBold = new boolean[width];

        java.util.Arrays.fill(characters, ' ');
    }

    public void setCell(int i, char c, TextAttributes attributes){
        setCharacter(i, c);
        setBackgroundColor(i, attributes.backgroundColor());
        setForegroundColor(i, attributes.foregroundColor());
        setItalic(i, attributes.isItalic());
        setBold(i, attributes.isBold());
        setUnderline(i, attributes.isUnderline());
    }

    public int getWidth(){
        return width;
    }

    public char getCharacter(int i){
        return characters[i];
    }

    public void setCharacter(int i, char c){
        characters[i] = c;
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

    @Override
    public String toString(){
        return new String(characters);
    }
}
