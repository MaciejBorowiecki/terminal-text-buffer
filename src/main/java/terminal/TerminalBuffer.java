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
    private final Screen screen;
    private final Scrollback scrollback;
    private TextAttributes currentAttributes;
    private final Cursor cursor;

    TerminalBuffer(int width, int height, int scrollbackSize) {
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
    TerminalBuffer(){
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
            Row droppedRow = screen.scrollUp();
            scrollback.addRow(droppedRow);
            cursor.setColumn(0);
        } else {
            cursor.moveDown(1);
            cursor.setColumn(0);
        }
    }

    /**
     * Write `c` at current cursor position, then move cursor.
     * @param c given character.
     */
    public void writeCharacter(char c) {
        int cCol = cursor.getColumn();
        int cRow = cursor.getRow();

        screen.setCell(cRow, cCol, c, currentAttributes);

        if (cCol == (screen.getWidth() - 1)) {
            handleNewLine();
        } else {
            cursor.moveRight(1);
        }
    }

    /**
     * Fill whole line at current cursor position, with character `c`.
     * @param c given character.
     */
    public void fillLine(char c) {
        for(int col = 0; col < screen.getWidth(); col++){
            screen.setCell(cursor.getRow(), col, c, currentAttributes);
        }
    }

    /**
     * Insert `text` into the buffer starting from current cursor position,
     * possibly wraps the line.
     * @param text given text.
     */
    public void insertText(String text) {
        for(int i = 0; i < text.length(); i++) {
            writeCharacter(text.charAt(i));
        }
    }

    /**
     * Insert empty line at the bottom of the screen,
     * doesn't affect current cursor position.
     */
    public void insertEmptyLine(){
        Row droppedRow = screen.scrollUp();
        scrollback.addRow(droppedRow);
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
    public char getCharacterAbsolute(int row, int col){
        int scrollBackSize = scrollback.getSize();
        if(row < scrollBackSize){
            return scrollback.getCharacterAt(row, col);
        } else {
            return screen.getCharacterAt(row - scrollBackSize, col);
        }
    }

    public char getCharacterAtScreen(int row, int col){
        return screen.getCharacterAt(row, col);
    }

    public char getCharacterAtScrollback(int row, int col){
        return scrollback.getCharacterAt(row, col);
    }

    public String getLineContentScreen(int row){
        return screen.getRowAt(row).toString();
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
}
