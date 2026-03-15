package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScreenTest {

    @Test
    void shouldThrowExceptionWhenAccessingOutOfBounds() {
        Screen screen = new Screen(80, 24);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterUnicodeAt(25, 0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterUnicodeAt(0, 85);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterUnicodeAt(-1, 30);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterUnicodeAt(3, -1);
        });
    }

    @Test
    void shouldScrollUpCorrectly() {
        Screen screen = new Screen(5, 3);
        TextAttributes attrs = new TextAttributes();

        screen.setCell(0, 0, 'A', attrs);
        screen.setCell(1, 0, 'B', attrs);

        Row topRow = screen.getTopRow();
        assertEquals('A', topRow.getCharacterUnicode(0));

        Row newBottom = new Row(5);
        newBottom.setCharacterUnicode(0, 'Z');

        screen.scrollUp(newBottom);

        assertEquals('B', screen.getCharacterUnicodeAt(0, 0));
        assertEquals('Z', screen.getCharacterUnicodeAt(2, 0));
    }
}
