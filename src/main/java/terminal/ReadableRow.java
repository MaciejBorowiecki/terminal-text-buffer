package terminal;

public interface ReadableRow {
    int getWidth();
    char getCharacter(int i);
    TextAttributes getTextAttributes(int i);

    @Override
    String toString();
}
