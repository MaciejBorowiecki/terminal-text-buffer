package terminal;

/**
 * Represents last N lines that scrolled off the top of the screen,
 * preserved for history and unmodifiable.
 */
public class Scrollback {
    private int size = 0;
    private final int capacity;
    private int head = 0; // oldest row in scrollback
    private final ReadableRow[] buffer;

    public Scrollback(int capacity) {
        this.capacity = capacity;
        this.buffer = new ReadableRow[capacity];
    }

    public Scrollback() {
        this(1000);
    }

    public int getCapacity() {
        return capacity;
    }

    public int getSize() {
        return size;
    }

    public int getHeadIndex() {
        return head;
    }

    public void clear(){
        head = 0;
        size = 0;
    }

    public ReadableRow getEvictedRow() {
        if (size == capacity){
            return buffer[head];
        }
        return null;
    }

    public void addRow(ReadableRow droppedRow) {
        if (size < capacity) {
            buffer[(head + size) % capacity] = droppedRow;
            size++;
        } else {
            buffer[head] = droppedRow;
            head = (head + 1) % capacity;
        }
    }

    /**
     * Checks whether given logical index is valid.
     * @param index logical index of the row (not taking into account cyclical shift)
     */
    private void checkLogicalIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid scrollback index: " + index);
        }
    }

    private void checkColumnIndex(int physicalRow, int col){
        if (col < 0 || col >= buffer[physicalRow].getWidth()) {
            throw new IndexOutOfBoundsException("Invalid column index: " + col);
        }
    }

    private int calculatePhysicalIndex(int rowIndex) {
        checkLogicalIndex(rowIndex);
        return (head + rowIndex) % capacity;
    }

    private int calculatePhysicalIndex(int row, int col) {
        int physicalRow = calculatePhysicalIndex(row);
        checkColumnIndex(physicalRow, col);
        return physicalRow;
    }

    public ReadableRow getRowAt(int logicalIndex) {
        int physicalIndex = calculatePhysicalIndex(logicalIndex);
        return buffer[physicalIndex];
    }

    public char getCharacterAt(int row, int col) {
        int physicalRow = calculatePhysicalIndex(row, col);
        return buffer[physicalRow].getCharacter(col);
    }

    public TextAttributes getTextAttributeAt(int row, int col) {
        int physicalRow = calculatePhysicalIndex(row, col);
        return buffer[physicalRow].getTextAttributes(col);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            int physicalIndex = calculatePhysicalIndex(i);
            ReadableRow row = buffer[physicalIndex];
            sb.append(row.toString());
            if(i < size - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
