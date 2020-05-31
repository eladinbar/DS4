public class UndoDeletionOperator implements UndoOperator {
    int pos;
    String element;

    UndoDeletionOperator(int pos, String element) {
        this.pos = pos;
        this.element = element;
    }

    @Override
    public void undo(CuckooHashing table) {
        String[] array = table.getArray();
        array[pos] = element;
    }
}
