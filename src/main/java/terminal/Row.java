package terminal;

/**
 * Represents a single line (horizontal line of text) on the terminal screen.
 * Stores characters and their attributes (background/text colors, styles) in parallel arrays.
 */

public class Row {
    private int width;
    private char[] characters;
    private byte[] bgColors;
    private byte[] fgColors;
    private boolean[] stylesItalic;
    private boolean[] stylesUnderline;
    private boolean[] stylesBold;

    public Row(int width){
        this.width = width;
        characters = new char[width];
        bgColors = new byte[width];
        fgColors = new byte[width];
        stylesItalic = new boolean[width];
        stylesUnderline = new boolean[width];
        stylesBold = new boolean[width];

        java.util.Arrays.fill(characters, ' ');
    }

    public void setCell(int i, char c, byte bgColor, byte fgColor, boolean italic,
                        boolean underline, boolean bold){
        setCharacter(i, c);
        setBackgroundColor(i, bgColor);
        setForegroundColor(i, fgColor);
        setItalic(i,italic);
        setBold(i, bold);
        setUnderline(i,underline);
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

    public byte getForegroundColor(int i){
        return fgColors[i];
    }

    public void setForegroundColor(int i, byte c){
        fgColors[i] = c;
    }

    public byte getBackgroundColor(int i){
        return bgColors[i];
    }

    public void setBackgroundColor(int i, byte c){
        bgColors[i] = c;
    }

    public boolean getUnderline(int i){
        return stylesUnderline[i];
    }

    public void setUnderline(int i, boolean b){
        stylesUnderline[i] = b;
    }

    public boolean getItalic(int i){
        return stylesItalic[i];
    }

    public void setItalic(int i, boolean b){
        stylesItalic[i] = b;
    }
    public boolean getBold(int i){
        return stylesBold[i];
    }

    public void setBold(int i, boolean b){
        stylesBold[i] = b;
    }

    @Override
    public String toString(){
        return new String(characters);
    }
}
