package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RowTest {

    @Test
    void shouldInitializeWithSpaces() {
        Row row = new Row(10);

        assertEquals(10, row.getWidth());
        for (int i = 0; i < 10; i++) {
            assertEquals(' ', row.getCharacterUnicode(i), "Empty cell should contain spaces.");
        }
        assertEquals("          ", row.toString());
    }

    @Test
    void shouldSetAndGetCellAttributes() {
        Row row = new Row(5);
        TextAttributes attrs = new TextAttributes((byte) 1, (byte) 2, true, false, true);

        row.setCell(2, 'X', attrs);

        assertEquals('X', row.getCharacterUnicode(2));

        TextAttributes retrievedAttrs = row.getTextAttributes(2);
        assertTrue(retrievedAttrs.isBold());
        assertFalse(retrievedAttrs.isItalic());
        assertTrue(retrievedAttrs.isUnderline());
        assertEquals((byte) 1, retrievedAttrs.foregroundColor());
        assertEquals((byte) 2, retrievedAttrs.backgroundColor());
    }

    @Test
    void shouldResetRowState() {
        Row row = new Row(5);
        TextAttributes attrs = new TextAttributes((byte) 1, (byte) 2, true, true, true);

        row.setCell(0, 'X', attrs);

        row.clear();

        assertEquals(' ', row.getCharacterUnicode(0));

        TextAttributes resetAttrs = row.getTextAttributes(0);
        assertFalse(resetAttrs.isBold());
        assertEquals((byte) 0, resetAttrs.foregroundColor());
        assertEquals((byte) 7, resetAttrs.backgroundColor());
    }

    @Test
    void shouldHandleWideCharactersAndDummyCells() {
        Row row = new Row(10);
        TextAttributes attrs = new TextAttributes();

        row.setCell(2, 0x1F680, attrs); // 🚀 (width 2)

        assertEquals(0x1F680, row.getCharacterUnicode(2), "The first cell should contain an emoji code");
        assertEquals(-1, row.getCharacterUnicode(3), "The second cell should be a blank (-1)");

        // Check if toString renders this correctly (skips -1)
        // \uD83D\uDE80 is the Java String equivalent of 🚀 (pair of chars)
        assertEquals("  \uD83D\uDE80      ", row.toString());
    }

    @Test
    void shouldClearOrphansWhenOverwritingWideCharacter() {
        Row row = new Row(10);
        TextAttributes attrs = new TextAttributes();

        // Case 1: Left Half Overwrite
        row.setCell(2, 0x1F680, attrs); // Character takes up cells 2 and 3
        row.setCell(2, 'X', attrs);     // destroy left

        assertEquals('X', row.getCharacterUnicode(2));
        assertEquals(' ', row.getCharacterUnicode(3), "Right half should be changed to ' '");

        // Case 2: Right half Overwrite
        row.setCell(5, 0x1F680, attrs); // Character takes up cells 5 and 6
        row.setCell(6, 'Y', attrs);

        assertEquals(' ', row.getCharacterUnicode(5), "Left half should be changed to ' '");
        assertEquals('Y', row.getCharacterUnicode(6));
    }
}
