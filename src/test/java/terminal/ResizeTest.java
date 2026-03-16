package terminal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResizeTest {

    private TerminalBuffer buffer;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(10, 5, 100);
    }

    @Test
    void shouldWrapTextWhenShrinkingWidth() {
       buffer.insertText("12345678");

        buffer.resize(5, 5);

        // First line should be wrapped
        assertEquals('1', buffer.getCharacterAbsolute(0, 0));
        assertEquals('5', buffer.getCharacterAbsolute(0, 4));

        //Second line (678)
        assertEquals('6', buffer.getCharacterAbsolute(1, 0));
        assertEquals('8', buffer.getCharacterAbsolute(1, 2));
        assertEquals(' ', buffer.getCharacterAbsolute(1, 3));
        // assertFalse(buffer.getScreen().isWrappedAt(1)); // This needs to be public fors tests
    }

    @Test
    void shouldTrackCursorCorrectlyDuringReflow() {
        buffer.insertText("Hello"); // Cursor is at col: 5 row: 0

        buffer.resize(3, 5);
        assertEquals(new Cursor.Position(0, 2), buffer.getCursorPosition());
    }

    @Test
    void shouldIgnoreTrailingSpacesWhenResizing() {
        buffer.insertText("Hi"); // Word "hi" then 8 default spaces

        buffer.resize(2, 5);

        assertEquals('H', buffer.getCharacterAbsolute(0, 0));
        assertEquals('i', buffer.getCharacterAbsolute(0, 1));

        assertEquals(' ', buffer.getCharacterAbsolute(1, 0));
        // assertFalse(buffer.getScreen().isWrappedAt(0)); // This needs to be public fors tests
    }

    @Test
    void shouldNotSplitWideCharactersAcrossLines() {
        // Write emoji at the last column (should go to the next line)
        buffer.insertText("123456789");
        buffer.writeCharacter(0x1F680); // 🚀

        buffer.resize(10, 4);

        assertEquals(' ', buffer.getCharacterAbsolute(0, 9));

        // Emoji is still at the beginning of the next line
        assertEquals(0x1F680, buffer.getCharacterAbsolute(1, 0));
    }

    @Test
    void shouldPushToScrollbackWhenReflowingCausesOverflow() {
        TerminalBuffer tinyBuffer = new TerminalBuffer(10, 2, 5);
        tinyBuffer.writeCharacter('A');
        tinyBuffer.writeCharacter('B');

        tinyBuffer.resize(1, 2);

        System.out.println(tinyBuffer.getScreenAndScrollbackContent());

        assertEquals('A', tinyBuffer.getCharacterAbsolute(0, 0));
        assertEquals('B', tinyBuffer.getCharacterAbsolute(1, 0));

        assertEquals('A', tinyBuffer.getCharacterAtScrollback(0, 0));
        assertEquals('B', tinyBuffer.getCharacterAtScreen(0, 0));
    }
}