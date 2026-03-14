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

    public void clear(){
        for(int i = 0; i < height; i++){
            rows[i] = new Row(width);
        }
    }

    private void checkBounds(int row){
        if (row < 0 || row >= height) {
            throw new IndexOutOfBoundsException("Row index outside of bounds: " + row);
        }
    }

    private void checkBounds(int row, int col) {
        checkBounds(row);
        if (col < 0 || col >= width) {
            throw new IndexOutOfBoundsException("Column index outside of bounds: " + col);
        }
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
        checkBounds(row, col);
        return rows[row].getCharacter(col);
    }

    public TextAttributes getTextAttributeAt(int row, int col) {
        checkBounds(row, col);
        return rows[row].getTextAttributes(col);
    }

    public Row getRowAt(int row) {
        checkBounds(row);
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
