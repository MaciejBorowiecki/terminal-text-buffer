package terminal;

public class Cursor {
    private int col;
    private int row;
    private int screenWidth;
    private int screenHeight;

    public Cursor(int col, int row, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        setPosition(row, col);
    }

    public Cursor(int screenWidth, int screenHeight) {
        this(0,0, screenWidth, screenHeight);
    }

    public record Position(int row, int col) {}

    public Position getPosition() {
        return new Position(row, col);
    }

    public int getColumn() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int col) {
        col = Math.max(0, col);
        col = Math.min(col, screenWidth - 1);
        this.col = col;
    }

    public void setRow(int row) {
        row = Math.max(0, row);
        row = Math.min(row, screenHeight - 1);
        this.row = row;
    }

    public void setPosition(int row, int col) {
        setColumn(col);
        setRow(row);
    }

    public void moveUp(int n){
        int newRow = row - n;
        setRow(newRow);
    }

    public void moveDown(int n){
        int newRow = row + n;
        setRow(newRow);
    }

    public void moveRight(int n){
        int newCol = col + n;
        setColumn(newCol);
    }

    public void moveLeft(int n) {
        int newCol = col - n;
        setColumn(newCol);
    }
}
