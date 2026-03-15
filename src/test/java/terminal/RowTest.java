package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RowTest {

    @Test
    void shouldInitializeWithSpaces() {
        Row row = new Row(10);

        assertEquals(10, row.getWidth());
        for (int i = 0; i < 10; i++) {
            assertEquals(' ', row.getCharacter(i), "Empty cell should contain spaces.");
        }
        assertEquals("          ", row.toString());
    }

    @Test
    void shouldSetAndGetCellAttributes() {
        Row row = new Row(5);
        TextAttributes attrs = new TextAttributes((byte) 1, (byte) 2, true, false, true);

        row.setCell(2, 'X', attrs);

        assertEquals('X', row.getCharacter(2));

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

        assertEquals(' ', row.getCharacter(0));

        TextAttributes resetAttrs = row.getTextAttributes(0);
        assertFalse(resetAttrs.isBold());
        assertEquals((byte) 0, resetAttrs.foregroundColor());
        assertEquals((byte) 7, resetAttrs.backgroundColor());
    }
}
