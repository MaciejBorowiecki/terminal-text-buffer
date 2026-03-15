package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TerminalBufferTest {

    @Test
    void shouldWriteCharacterAndMoveCursorRight() {
        TerminalBuffer buffer = new TerminalBuffer(80, 24, 100);

        // write at the current cursor position (default cursor position is (0,0) - top left corner)
        buffer.writeCharacter('H');
        buffer.writeCharacter('i');
        buffer.moveCursorRight(2);
        buffer.writeCharacter('j');

        assertEquals('H', buffer.getCharacterAtScreen(0, 0));
        assertEquals('i', buffer.getCharacterAtScreen(0, 1));
        assertEquals(' ', buffer.getCharacterAtScreen(0, 2));
        assertEquals(' ', buffer.getCharacterAtScreen(0, 3));
        assertEquals('j', buffer.getCharacterAtScreen(0, 4));
    }

    @Test
    void shouldWrapLineWhenReachingRightEdge() {
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 100);
        buffer.setCursorPosition(0, 9);

        buffer.writeCharacter('X');
        buffer.writeCharacter('Y');

        assertEquals('X', buffer.getCharacterAtScreen(0, 9));
        assertEquals('Y', buffer.getCharacterAtScreen(1, 0));

        buffer.insertText("AAAAAAAAAAAAA");
        assertEquals('A', buffer.getCharacterAtScreen(2, 0));
        assertEquals(new Cursor.Position(2,4), buffer.getCursorPosition());
    }

    @Test
    void shouldPushToScrollbackWhenScrollingDown() {
        TerminalBuffer buffer = new TerminalBuffer(10, 2, 100);

        buffer.fillLine('A');
        buffer.insertEmptyLine();
        buffer.fillLine('B');

        assertEquals("AAAAAAAAAA", buffer.getLineContentScrollback(0), "Line A should be in the scrollback at this point");
        assertEquals("BBBBBBBBBB", buffer.getLineContentScreen(0), "Line B should be at the top of the screen at this point");
    }

    @Test
    void shouldFetchAbsoluteCoordinatesCorrectly() {
        TerminalBuffer buffer = new TerminalBuffer(10, 2, 100);
        buffer.fillLine('X');
        buffer.insertEmptyLine();
        buffer.fillLine('Y');

        // Scrollback(0) = 'X'
        // Screen(0) = 'Y'
        // Screen(1) = empty

        assertEquals('X', buffer.getCharacterAbsolute(0, 0), "Index 0 is the oldest row in history");
        assertEquals('Y', buffer.getCharacterAbsolute(1, 0),
                "Index 1 is the beginning of the screen (because scrollback has size 1)");
    }

    @Test
    void shouldClearScreenAndResetCursor() {
        TerminalBuffer buffer = new TerminalBuffer(10, 5, 100);
        buffer.setCursorPosition(5, 5); // cursor clamping
        buffer.writeCharacter('Z');

        buffer.clearScreen();

        assertEquals(' ', buffer.getCharacterAtScreen(4, 5));
        assertEquals(new Cursor.Position(0,0), buffer.getCursorPosition());
    }
}
