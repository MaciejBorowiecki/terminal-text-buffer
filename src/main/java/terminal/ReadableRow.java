package terminal;

public interface ReadableRow {
    int getWidth();
    int getCharacterUnicode(int i);
    TextAttributes getTextAttributes(int i);

    @Override
    String toString();
}
