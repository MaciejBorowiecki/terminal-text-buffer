package terminal;

/**
 * Represents the last N (height) lines that fit the screen dimensions.
 */

public class Screen {
    private final int width;
    private final int height;
    private final Row[] rows;

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
            rows[i].clear();
        }
    }

    private void checkBounds(int row){
        if (row < 0 || row >= height) {
            throw new IndexOutOfBoundsException("Row index outside of bounds: " + row);
        }
    }

    /**
     * Moves whole screen 1 row upwards.
     * @param recycledRow Oldest row in the scrollback to reuse, or null if scrollback is not full.
     */
    public void scrollUp(Row recycledRow) {
        System.arraycopy(rows, 1, rows, 0, height - 1);
        rows[height - 1] = recycledRow;
    }

    public Row getTopRow(){
        return rows[0];
    }

    private Row getRowAt(int row){
        checkBounds(row);
        return rows[row];
    }

    public void setCell(int row, int col, int uniCode, TextAttributes textAttributes) {
        getRowAt(row).setCell(col, uniCode, textAttributes);
    }

    public void setCell(int row, int col, int codePoint, byte fg, byte bg,
                        boolean bold, boolean italic, boolean underline) {
        getRowAt(row).setCell(col, codePoint, fg, bg, bold, italic, underline);
    }

    public void setWrapped(int row){
        getRowAt(row).setWrapped(true);
    }

    public int getCharacterUnicodeAt(int row, int col) {
        return getRowAt(row).getCharacterUnicode(col);
    }

    public TextAttributes getTextAttributeAt(int row, int col) {
        return getRowAt(row).getTextAttributes(col);
    }

    public String getRowString(int row) {
        return getRowAt(row).toString();
    }

    public boolean isWrappedAt(int row) {
        return getRowAt(row).isWrapped();
    }

    public byte getForegroundColorAt(int row, int col) {
        return getRowAt(row).getForegroundColorAt(col);
    }

    public byte getBackgroundColorAt(int row, int col) {
        return getRowAt(row).getBackgroundColorAt(col);
    }

    public boolean isBoldAt(int row, int col) {
        return getRowAt(row).isBoldAt(col);
    }

    public boolean isItalicAt(int row, int col) {
        return getRowAt(row).isItalicAt(col);
    }

    public boolean isUnderlineAt(int row, int col) {
        return getRowAt(row).isUnderlineAt(col);
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
