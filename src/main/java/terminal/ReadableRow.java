package terminal;

public interface ReadableRow {
    int getWidth();
    char getCharacter(int i);
    byte getForegroundColor(int i);
    byte getBackgroundColor(int i);
    boolean getItalic(int i);
    boolean getBold(int i);
    boolean getUnderline(int i);

    @Override
    String toString();
}
