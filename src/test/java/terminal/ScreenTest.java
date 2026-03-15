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

        Row droppedRow = screen.scrollUp();

        assertEquals('A', droppedRow.getCharacter(0), "The returned row should be the one at the very top");
        assertEquals('B', screen.getCharacterAt(0, 0), "Second row should be the one at the very top");
        assertEquals(' ', screen.getCharacterAt(2, 0), "New row at the bottom should be empty");
    }
}
