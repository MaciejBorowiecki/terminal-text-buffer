package terminal;

public interface ReadableRow {
    int getWidth();
    int getCharacterUnicode(int i);
    TextAttributes getTextAttributes(int i);
    boolean isWrapped();
    byte getForegroundColorAt(int i);
    byte getBackgroundColorAt(int i);
    boolean isBoldAt(int i);
    boolean isItalicAt(int i);
    boolean isUnderlineAt(int i);

    @Override
    String toString();
}
