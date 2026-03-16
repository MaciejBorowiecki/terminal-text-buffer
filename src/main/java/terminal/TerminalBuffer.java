package terminal;

/**
 * The core data structure of the terminal emulator.
 * <p>
 * This buffer manages a grid of character cells, divided into two logical parts:
 * the active screen (editable area visible to the user) and the scrollback
 * (unmodifiable history of lines that scrolled off the top).
 * It also maintains the current cursor position and text attributes for writing.
 */
public class TerminalBuffer {
    private Screen screen;
    private Scrollback scrollback;
    private TextAttributes currentAttributes;
    private final Cursor cursor;

    public TerminalBuffer(int width, int height, int scrollbackSize) {
        screen = new Screen(width, height);
        scrollback = new Scrollback(scrollbackSize);
        currentAttributes = new TextAttributes();
        cursor = new Cursor(width, height);
    }

    /**
     * Initialized with default values:
     * <ul>
     * <li><b>screen width:</b> 80 columns; screen height: 24 rows</li>
     * <li><b>scrollback maximum size: </b>1000 rows</li>
     * <li><b>cursor position:</b> (0,0) (top left corner)</li>
     * <li><b>styles:</b> foreground white, background black, no styles applied</li>
     *  </ul>
     */
    public TerminalBuffer(){
        screen = new Screen();
        scrollback = new Scrollback();
        currentAttributes = new TextAttributes();
        cursor = new Cursor(screen.getWidth(), screen.getHeight());
    }

    public Cursor.Position getCursorPosition() {
        return cursor.getPosition();
    }

    /**
     * Decide whether screen needs to scroll upwards or there is space for another row.
     */
    private void handleNewLine(){
        if (cursor.getRow() == (screen.getHeight() - 1)) {
            handleScrollUp(screen, scrollback);
        } else {
            cursor.moveDown(1);
        }
        cursor.setColumn(0);
    }

    private void handleScrollUp(Screen screen, Scrollback scrollback) {
        Row topScreenRow = screen.getTopRow();
        Row recycledRow = (Row) scrollback.getEvictedRow();

        scrollback.addRow(topScreenRow);

        if(recycledRow == null) {
            recycledRow = new Row(screen.getWidth());
        } else {
            recycledRow.clear();
        }
        screen.scrollUp(recycledRow);
    }

    /**
     * Write `c` at current cursor position, then move cursor.
     * @param uniCode given character.
     */
    public void writeCharacter(int uniCode) {
        int width = getNormalizedCharWidth(uniCode);
        if(width == 0) return; // ignore characters of 0 width

        int cCol = cursor.getColumn();
        int cRow = cursor.getRow();

        // If character is wider than space left on the line - write it on the next one.
        if (width == 2 && cCol == screen.getWidth() - 1) {
            screen.setCell(cRow, cCol, ' ', currentAttributes);

            screen.setWrapped(cRow);
            handleNewLine();

            cCol = cursor.getColumn();
            cRow = cursor.getRow();
        }

        screen.setCell(cRow, cCol, uniCode, currentAttributes);
        if (cCol == (screen.getWidth() - 1)) {
            screen.setWrapped(cRow);
            handleNewLine();
        } else {
            cursor.moveRight(width);
        }
    }

    /**
     * Fill whole line at current cursor position, with codePoint.
     * @param codePoint given codePoint character.
     */
    public void fillLine(int codePoint) {
        int width = getNormalizedCharWidth(codePoint);
        if (width == 0) return;

        int screenWidth = screen.getWidth();
        int row = cursor.getRow();

        for (int col = 0; col < screenWidth; col += width) {
            if (width == 2 && col == screenWidth - 1) {
                screen.setCell(row, col, ' ', currentAttributes);
            } else {
                screen.setCell(row, col, codePoint, currentAttributes);
            }
        }
    }

    /**
     * Insert `text` into the buffer starting from current cursor position,
     * possibly wraps the line. Handles wide characters and surrogate pairs.
     * @param text given text.
     */
    public void insertText(String text) {
        text.codePoints().forEach(this::writeCharacter);
    }

    /**
     * Insert empty line at the bottom of the screen,
     * doesn't affect current cursor position.
     */
    public void insertEmptyLine(){
        handleScrollUp(screen, scrollback);
    }

    /**
     * Clears entire screen content without saving it to the scrollback.
     * Moves the cursor to the beginning of the screen.
     */
    public void clearScreen(){
        screen.clear();
        cursor.setPosition(0,0);
    }

    /**
     * Clears entire screen and scrollback content.
     * Moves the cursor to the beginning of the screen.
     */
    public void clearScreenAndScrollback() {
        clearScreen();
        scrollback.clear();
    }

    /**
     * Returns character that is at given position. <br>
     * @param row row of the character. Lower row number means older row
     *           (possibly from scrollback). Last N rows are screen rows,
     *            where N is the height of the screen.
     * @param col column of the character.
     * @return character at (row, col) position.
     */
    public int getCharacterAbsolute(int row, int col){
        int scrollBackSize = scrollback.getSize();
        if(row < scrollBackSize){
            return scrollback.getCharacterUnicodeAt(row, col);
        } else {
            return screen.getCharacterUnicodeAt(row - scrollBackSize, col);
        }
    }

    public int getCharacterAtScreen(int row, int col){
        return screen.getCharacterUnicodeAt(row, col);
    }

    public int getCharacterAtScrollback(int row, int col){
        return scrollback.getCharacterUnicodeAt(row, col);
    }

    public String getLineContentScreen(int row){
        return screen.getRowString(row);
    }

    /**
     * Returns line content as a string.
     * @param row Row number of the wanted line. Lower number means older row
     *            (possibly from scrollback). Last N rows are screen rows,
     *            where N is the height of the screen.
     * @return wanted line.
     */
    public String getLineAbsolute(int row){
        int scrollbackSize = scrollback.getSize();
        if(row < scrollbackSize){
            return getLineContentScrollback(row);
        } else {
            return getLineContentScreen(row - scrollbackSize);
        }
    }

    public String getLineContentScrollback(int row){
        return scrollback.getRowAt(row).toString();
    }

    public String getScreenContent(){
        return screen.toString();
    }

    public String getScreenAndScrollbackContent(){
        StringBuilder sb = new StringBuilder();
        sb.append(scrollback.toString());
        sb.append("\n");
        sb.append(screen.toString());
        return sb.toString();
    }

    public TextAttributes getCurrentAttributes(){
        return currentAttributes;
    }

    /**
     * Returns styles at given coordinate.
     * @param row Row number of the wanted coordinates. Lower number means older row
     *      *            (possibly from scrollback). Last N rows are screen rows,
     *      *            where N is the height of the screen.
     * @param col Column number of the wanted coordinates.
     * @return TextAttributes object with cell attributes.
     */
    public TextAttributes getAttributesAbsolute(int row, int col){
        int scrollBackSize = scrollback.getSize();
        if(row < scrollBackSize){
            return getScrollbackAttributesAt(row, col);
        } else {
            return getScreenAttributesAt(row - scrollBackSize, col);
        }
    }

    public TextAttributes getScrollbackAttributesAt(int row, int col){
        return scrollback.getTextAttributeAt(row, col);
    }

    public TextAttributes getScreenAttributesAt(int row, int col){
        return screen.getTextAttributeAt(row, col);
    }

    public void setAttributes(TextAttributes attributes){
        currentAttributes = attributes;
    }

    /**
     * Sets the cursor position on the screen.
     * <p>
     * (0,0) represents the top-left corner. If the provided coordinates are
     * outside the screen boundaries, the cursor position will be clamped to
     * the nearest valid edge instead of throwing an exception.
     * @param row target row index.
     * @param col target column index.
     */
    public void setCursorPosition(int row, int col){
        cursor.setPosition(row, col);
    }

    public void moveCursorUp(int n) {
        cursor.moveUp(n);
    }

    public void moveCursorDown(int n) {
        cursor.moveDown(n);
    }

    public void moveCursorLeft(int n) {
        cursor.moveLeft(n);
    }

    public void moveCursorRight(int n) {
        cursor.moveRight(n);
    }

    /**
     * Helper class to hold the state of the "Writer" during the reflow process.
     */
    private static class ReflowState {
        int writeRow = 0;
        int writeCol = 0;
        int newCursorRow = -1;
        int newCursorCol = -1;
    }

    public void resize(int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) return;
        if (newWidth == screen.getWidth() && newHeight == screen.getHeight()) return;

        Screen newScreen = new Screen(newWidth, newHeight);
        Scrollback newScrollback = new Scrollback(scrollback.getCapacity());
        ReflowState state = new ReflowState();

        int oldWidth = screen.getWidth();

        // Read old history and pump into new buffers
        processScrollback(newWidth, newHeight, oldWidth, newScreen, newScrollback, state);

        // Read old screen and pump into new buffers
        processScreen(newWidth, newHeight, oldWidth, newScreen, newScrollback, state);

        // Swap buffers and apply new cursor position
        finalizeResize(newWidth, newHeight, newScreen, newScrollback, state);
    }

    /**
     * Rewrite scrollback into new dimensions.
     */
    private void processScrollback(int newWidth, int newHeight, int oldWidth,
                                   Screen newScreen, Scrollback newScrollback,
                                   ReflowState state) {
        int oldScrollbackSize = scrollback.getSize();

        for (int r = 0; r < oldScrollbackSize; r++) {
            int logicalEnd = getLogicalEndIndexScrollback(r, oldWidth);

            for (int c = 0; c < logicalEnd; c++) {
                int codePoint = scrollback.getCharacterUnicodeAt(r, c);
                if (codePoint == -1) continue;

                int charWidth = getNormalizedCharWidth(codePoint);
                if (charWidth == 0) continue;

                // Wrap line if character doesn't fit (Soft wrap)
                if (state.writeCol + charWidth > newWidth) {
                    advanceWriterRow(newWidth, newHeight, newScreen,
                            newScrollback, state, true);
                }

                byte fg = scrollback.getForegroundColorAt(r, c);
                byte bg = scrollback.getBackgroundColorAt(r, c);
                boolean bold = scrollback.isBoldAt(r, c);
                boolean italic = scrollback.isItalicAt(r, c);
                boolean underline = scrollback.isUnderlineAt(r, c);

                newScreen.setCell(state.writeRow, state.writeCol, codePoint,
                        fg, bg, bold, italic, underline);
                state.writeCol += charWidth;
            }

            // Handle hard enter at the end of the old row
            if (!scrollback.isWrappedAt(r)) {
                advanceWriterRow(newWidth, newHeight, newScreen, newScrollback,
                        state, false);
            }
        }
    }

    private void processScreen(int newWidth, int newHeight, int oldWidth,
                               Screen newScreen, Scrollback newScrollback,
                               ReflowState state) {
        int oldHeight = screen.getHeight();
        int oldCursorRow = cursor.getRow();
        int oldCursorCol = cursor.getColumn();

        int maxUsedRow = oldHeight - 1;
        while (maxUsedRow >= 0
                && getLogicalEndIndexScreen(maxUsedRow, oldWidth,
                maxUsedRow == oldCursorRow, oldCursorCol) == 0
                && maxUsedRow != oldCursorRow) {
            maxUsedRow--;
        }

        for (int r = 0; r <= maxUsedRow; r++) {
            boolean hasCursor = (r == oldCursorRow);
            int logicalEnd = getLogicalEndIndexScreen(r, oldWidth, hasCursor, oldCursorCol);

            for (int c = 0; c < logicalEnd; c++) {

                // Catch the old cursor position
                if (hasCursor && c == oldCursorCol) {
                    state.newCursorRow = state.writeRow;
                    state.newCursorCol = state.writeCol;
                }

                int codePoint = screen.getCharacterUnicodeAt(r, c);
                if (codePoint == -1) continue;

                int charWidth = getNormalizedCharWidth(codePoint);
                if (charWidth == 0) continue;

                // Wrap line if character doesn't fit
                if (state.writeCol + charWidth > newWidth) {
                    advanceWriterRow(newWidth, newHeight, newScreen, newScrollback,
                            state, true);
                }

                byte fg = screen.getForegroundColorAt(r, c);
                byte bg = screen.getBackgroundColorAt(r, c);
                boolean bold = screen.isBoldAt(r, c);
                boolean italic = screen.isItalicAt(r, c);
                boolean underline = screen.isUnderlineAt(r, c);

                newScreen.setCell(state.writeRow, state.writeCol, codePoint,
                        fg, bg, bold, italic, underline);
                state.writeCol += charWidth;
            }

            // Catch cursor if it was standing right after the last character
            if (hasCursor && oldCursorCol >= logicalEnd) {
                state.newCursorRow = state.writeRow;
                state.newCursorCol = state.writeCol;
            }

            // Handle hard enter at the end of the old row
            if (!screen.isWrappedAt(r)) {
                advanceWriterRow(newWidth, newHeight, newScreen, newScrollback,
                        state, false);
            }
        }
    }

    /**
     * Moves the writer's position to the next line.
     * Handles pushing overflow lines from the new screen into the new scrollback.
     */
    private void advanceWriterRow(int newWidth, int newHeight, Screen newScreen,
                                  Scrollback newScrollback, ReflowState state,
                                  boolean isSoftWrap) {
        if (isSoftWrap) {
            newScreen.setWrapped(state.writeRow);
        }

        state.writeRow++;
        state.writeCol = 0;

        // If we exceeded the new screen height, scroll it up and save the top row to history
        if (state.writeRow >= newHeight) {
            handleScrollUp(newScreen, newScrollback);
            state.writeRow = newHeight - 1;

            // Adjust cursor position if it has already been placed
            if (state.newCursorRow != -1) {
                state.newCursorRow--;
            }
        }
    }

    private void finalizeResize(int newWidth, int newHeight, Screen newScreen,
                                Scrollback newScrollback, ReflowState state) {
        this.screen = newScreen;
        this.scrollback = newScrollback;

        // Sanity check to prevent out-of-bounds cursor
        if (state.newCursorRow < 0) state.newCursorRow = 0;
        if (state.newCursorRow >= newHeight) state.newCursorRow = newHeight - 1;
        if (state.newCursorCol < 0) state.newCursorCol = 0;
        if (state.newCursorCol >= newWidth) state.newCursorCol = newWidth - 1;

        this.cursor.setPosition(state.newCursorRow, state.newCursorCol);
    }

    private int getNormalizedCharWidth(int codePoint) {
        int charWidth = WcWidth.calculateWidth(codePoint);
        if (charWidth == 0 && codePoint == ' ') return 1;
        if (charWidth == 0 && codePoint != ' ') return 0;
        return charWidth;
    }

    /**
     * Find last non-empty character index in the row.
     * @param r row number.
     * @param oldWidth old row width.
     * @return last non-empty character index.
     */
    private int getLogicalEndIndexScrollback(int r, int oldWidth) {
        if (scrollback.isWrappedAt(r)) return oldWidth;

        for (int c = oldWidth - 1; c >= 0; c--) {
            if (scrollback.getCharacterUnicodeAt(r, c) != ' ') {
                return c + 1;
            }
        }
        return 0;
    }

    /**
     * Find last non-empty character index in the row.
     * Cursor position counts as non-empty character cell no metter its content.
     * @param r row number.
     * @param oldWidth old row width.
     * @param hasCursor is cursor in this row.
     * @param oldCursorCol what was cursor column position with old width.
     * @return last non-empty character index.
     */
    private int getLogicalEndIndexScreen(int r, int oldWidth, boolean hasCursor,
                                         int oldCursorCol) {
        int logicalEnd = oldWidth;

        if (!screen.isWrappedAt(r)) {
            logicalEnd = 0;
            for (int c = oldWidth - 1; c >= 0; c--) {
                if (screen.getCharacterUnicodeAt(r, c) != ' ') {
                    logicalEnd = c + 1;
                    break;
                }
            }
        }

        return logicalEnd;
    }
}
