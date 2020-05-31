public class UndoKickOperator implements UndoOperator {

    private String value;
    private int index;


    public UndoKickOperator(String value, int index){
        this.value = value;
        this.index = index;
    }
    @Override
    public void undo(CuckooHashing table) {
        String[] elements = table.getArray();
        elements[index] = value;
    }
}
