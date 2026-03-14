package src.main.java;

public class Cursor {
    private int col = 0;
    private int row = 0;
    private int screenWidth;
    private int screenHeight;

    public Cursor(int col, int row, int screenWidth, int screenHeight) {
        setColumn(col);
        setRow(row);
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public Cursor(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public record Position(int col, int row) {}

    public Position getPosition() {
        return new Position(col, row);
    }

    public int getColumn() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public void setColumn(int col) {
        col = Math.max(0, col);
        col = Math.min(col, screenWidth);
        this.col = col;
    }

    public void setRow(int row) {
        row = Math.max(0, row);
        row = Math.min(row, screenHeight);
        this.row = row;
    }

    public void setPosition(int col, int row) {
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
