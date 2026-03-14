package terminal;

/**
 * Represents the last N (height) lines that fit the screen dimensions.
 */

public class Screen {
    private int width;
    private int height;
    private Row[] rows;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        rows = new Row[height];

        for (int i = 0; i < height; i++) {
            this.rows[i] = new Row(width);
        }
    }

    public Screen() {
        this(80,24);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Moves whole screen 1 row upwards.
     * @return Row that has moved outside screen boundaries.
     */
    public Row scrollUp() {
        Row oldestRow = rows[0];
        System.arraycopy(rows, 1, rows, 0, height - 1);
        rows[height - 1] = new Row(width);
        return oldestRow;
    }

    public char getCharacterAt(int row, int col) {
        return rows[row].getCharacter(col);
    }

    public byte getForegroundColorAt(int row, int col) {
        return rows[row].getForegroundColor(col);
    }

    public byte getBackgroundColorAt(int row, int col) {
        return rows[row].getBackgroundColor(col);
    }

    public boolean getItalicAt(int row, int col) {
        return rows[row].getItalic(col);
    }

    public boolean getBoldAt(int row, int col) {
        return rows[row].getBold(col);
    }

    public boolean getUnderlineAt(int row, int col) {
        return rows[row].getUnderline(col);
    }

    public Row getRowAt(int row) {
        return rows[row];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < height; i++) {
            sb.append(rows[i].toString());
            if (i < height - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
