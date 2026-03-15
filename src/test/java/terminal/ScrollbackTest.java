package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ScrollbackTest {

    @Test
    void shouldAddRowsWithoutWrapping() {
        Scrollback scrollback = new Scrollback(5);
        Row row1 = new Row(10);
        row1.setCharacter(0, '1');

        scrollback.addRow(row1);

        assertEquals(1, scrollback.getSize());
        assertEquals('1', scrollback.getRowAt(0).getCharacter(0));
    }

    @Test
    void shouldWrapAroundAndReturnEvictedRow() {
        Scrollback scrollback = new Scrollback(3);

        Row r1 = new Row(5); r1.setCharacter(0, 'A');
        Row r2 = new Row(5); r2.setCharacter(0, 'B');
        Row r3 = new Row(5); r3.setCharacter(0, 'C');
        Row r4 = new Row(5); r4.setCharacter(0, 'D');

        scrollback.addRow(r1);
        scrollback.addRow(r2);
        scrollback.addRow(r3);

        ReadableRow evictedRow = scrollback.getEvictedRow();
        assertNotNull(evictedRow, "Bufor is full, should return row for recycling.");
        assertEquals('A', evictedRow.getCharacter(0), "It should be oldest row ('A')");

        scrollback.addRow(r4);

        assertEquals(3, scrollback.getSize());
        assertEquals('B', scrollback.getRowAt(0).getCharacter(0));
    }

    @Test
    void shouldThrowExceptionForInvalidLogicalIndex() {
        Scrollback scrollback = new Scrollback(5);
        scrollback.addRow(new Row(10));

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getRowAt(1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getRowAt(4);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getRowAt(-1);
        });
    }

    @Test
    void shouldThrowErrorForAccessingEmptyScrollback() {
        Scrollback scrollback = new Scrollback(5);

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getRowAt(0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getCharacterAt(0, 0);
        });

        scrollback.addRow(new Row(10));
        assertEquals(1, scrollback.getSize(), "There should be 1 row in scrollback");
        scrollback.clear();
        assertEquals(0, scrollback.getSize(), "There should be no rows in scrollback");

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getRowAt(0);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getCharacterAt(0, 0);
        });
    }

    @Test
    void shouldThrowExceptionForInvalidColumnIndex() {
        Scrollback scrollback = new Scrollback(5);
        scrollback.addRow(new Row(10));

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getCharacterAt(0,-1);
        });

        assertThrows(IndexOutOfBoundsException.class, () -> {
            scrollback.getCharacterAt(0,11);
        });
    }
}
