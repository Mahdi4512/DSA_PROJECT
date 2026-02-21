package structures;
public class UndoStack {
    private StackNode top;
    private int size;
    private class StackNode {
        Undo action;
        StackNode next;
        StackNode(Undo action) {
            this.action = action;
            this.next = null;
        }
    }

    public UndoStack() {
        top = null;
        size = 0;
    }

    public void push(Undo action) {
        StackNode newNode = new StackNode(action);
        newNode.next = top;
        top = newNode;
        size++;
    }

    public Undo pop() {
        if (isEmpty()) return null;
        Undo action = top.action;
        top = top.next;
        size--;
        return action;
    }

    public Undo peek() {
        if (isEmpty()) return null;
        return top.action;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }//برای اینکه اگه کاربر سوار دستگاه شد عملیات undo که در پشته ذخیره شده بود باید حذف بشود
    public boolean removeJoinQueueAction(int visitorId, String rideName) {
        StackNode current = top;
        StackNode prev = null;
        while (current != null) {
            if (current.action.command.equals("LEAVE_QUEUE") &&
                    current.action.id == visitorId &&
                    current.action.rideName != null &&
                    current.action.rideName.equals(rideName)) {

                if (prev == null) {
                    top = current.next;
                } else {
                    prev.next = current.next;
                }

                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }

        return false;
    }
}