import java.util.Stack;

public class UndoInsertionOperator implements UndoOperator {

    private Stack<UndoKickOperator> undoKickOperatorStack = new Stack<>();

    @Override
    public void undo(CuckooHashing table) {
        while(!undoKickOperatorStack.empty()){
            undoKickOperatorStack.pop().undo(table);
        }
    }

    public void add(String value, int index){
        undoKickOperatorStack.push(new UndoKickOperator(value,index));
    }

}
