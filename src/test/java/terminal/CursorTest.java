package terminal;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CursorTest {

    @Test
    void shouldClampCursorToScreenBoundaries() {
        Cursor cursor = new Cursor(80, 24);

        cursor.setPosition(50, 100);

        assertEquals(79, cursor.getColumn(), "Cursor should lock on last column (79)");
        assertEquals(23, cursor.getRow(), "Cursor should lock on last row (23)");

        cursor.setPosition(-1,-5);

        assertEquals(0, cursor.getColumn(), "Cursor should lock on first column (0)");
        assertEquals(0, cursor.getRow(), "Cursor should lock on first row (0)");
    }

    @Test
    void shouldNotMoveBelowZero() {
        Cursor cursor = new Cursor(10, 10);
        cursor.moveLeft(50);
        cursor.moveUp(50);

        assertEquals(0, cursor.getColumn());
        assertEquals(0, cursor.getRow());
    }

    @Test void shouldMoveAndSetCorrectly(){
        Cursor cursor = new Cursor(10, 10);
        assertEquals(new Cursor.Position(0,0), cursor.getPosition());
        cursor.moveRight(2);
        assertEquals(new Cursor.Position(0,2), cursor.getPosition());
        cursor.moveDown(3);
        assertEquals(new Cursor.Position(3,2), cursor.getPosition());
        cursor.moveLeft(2);
        assertEquals(new Cursor.Position(3,0), cursor.getPosition());
        cursor.moveUp(3); // cursor is back at the (0,0) position
        assertEquals(new Cursor.Position(0,0), cursor.getPosition());

        cursor.setPosition(5,5);
        assertEquals(new Cursor.Position(5,5), cursor.getPosition());
        cursor.setColumn(3);
        assertEquals(new Cursor.Position(5,3), cursor.getPosition());
        cursor.setRow(9);
        assertEquals(new Cursor.Position(9,3), cursor.getPosition());
    }
}