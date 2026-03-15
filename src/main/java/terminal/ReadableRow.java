package terminal;

public interface ReadableRow {
    int getWidth();
    int getCharacterUnicode(int i);
    TextAttributes getTextAttributes(int i);
    boolean isWrapped();

    @Override
    String toString();
}
