package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScreenTest {

    @Test
    void shouldThrowExceptionWhenAccessingOutOfBounds() {
        Screen screen = new Screen(80, 24);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterAt(25, 0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterAt(0, 85);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterAt(-1, 30);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            screen.getCharacterAt(3, -1);
        });
    }

    @Test
    void shouldScrollUpCorrectly() {
        Screen screen = new Screen(5, 3);
        TextAttributes attrs = new TextAttributes();

        screen.setCell(0, 0, 'A', attrs);
        screen.setCell(1, 0, 'B', attrs);

        Row topRow = screen.getTopRow();
        assertEquals('A', topRow.getCharacter(0));

        Row newBottom = new Row(5);
        newBottom.setCharacter(0, 'Z');

        screen.scrollUp(newBottom);

        assertEquals('B', screen.getCharacterAt(0, 0));
        assertEquals('Z', screen.getCharacterAt(2, 0));
    }
}
