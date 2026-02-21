package structures;

public class queue {
    private Node front;
    private Node rear;
    private int size;
    private String rideName;

    private class Node {
        Visitor visitor;
        Node next;

        Node(Visitor visitor) {
            this.visitor = visitor;
            this.next = null;
        }
    }

    public queue(String rideName) {
        this.front = null;
        this.rear = null;
        this.size = 0;
        this.rideName = rideName;
    }

    // اضافه کردن با اولویت VIP
    public boolean enqueue(Visitor visitor) {
        Node newNode = new Node(visitor);

        if (isEmpty()) {
            front = rear = newNode;
        } else if (visitor.isVIP()) {

            Node current = front;
            Node lastVIP = null;

            // پیدا کردن آخرین VIP
            while (current != null && current.visitor.isVIP()) {
                lastVIP = current;
                current = current.next;
            }

            if (lastVIP != null) {

                newNode.next = lastVIP.next;
                lastVIP.next = newNode;
                if (lastVIP == rear) {
                    rear = newNode;
                }
            } else {

                newNode.next = front;
                front = newNode;
            }
        } else {
            // افراد عادی به انتها اضافه می‌شوند
            rear.next = newNode;
            rear = newNode;
        }

        size++;
        return true;
    }
   public void repositionVip(int id){
        Node p=front;
        Node pre=null;
        while(p!=null && p.visitor.getId()!=id){
            pre=p;
            p=p.next;
        }
        if(p==null || !p.visitor.isVIP()){
            return;
        }
        Visitor visitor=p.visitor;
        if(pre==null){
            front=p.next;
            if(front==null){
                rear=null;
            }}else{
                pre.next=p.next;
                if(p==rear){
                    rear=pre;
                }
            }
        size-=1;
        enqueue(visitor);
   }
    public Visitor dequeue() {
        if (isEmpty()) return null;

        Visitor visitor = front.visitor;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        size--;
        return visitor;
    }

    public Visitor[] takeVisitors(int count) {
        int actualCount = Math.min(count, size);
        Visitor[] visitors = new Visitor[actualCount];

        for (int i = 0; i < actualCount; i++) {
            visitors[i] = dequeue();
        }

        return visitors;
    }

    public boolean removeVisitor(int visitorId) {
        if (isEmpty()) return false;

        // اگر اولین نفر باشد
        if (front.visitor.getId() == visitorId) {
            front = front.next;
            if (front == null) {
                rear = null;
            }
            size--;
            return true;
        }

        // جستجو در بقیه
        Node current = front;
        while (current.next != null) {
            if (current.next.visitor.getId() == visitorId) {
                current.next = current.next.next;
                if (current.next == null) {
                    rear = current;
                }
                size--;
                return true;
            }
            current = current.next;
        }

        return false;
    }

    public int getVisitorPosition(int visitorId) {
        Node current = front;
        int position = 0;
        while (current != null) {
            if (current.visitor.getId() == visitorId) {
                return position;
            }
            current = current.next;
            position++;
        }

        return -1;
    }

    public boolean insertAtPosition(Visitor visitor, int position) {
        if (position < 0 || position > size) {
            return false;
        }

        Node newNode = new Node(visitor);

        if (position == 0) {
            newNode.next = front;
            front = newNode;
            if (rear == null) {
                rear = newNode;
            }
        } else {
            Node current = front;
            for (int i = 0; i < position - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;

            if (newNode.next == null) {
                rear = newNode;
            }
        }

        size++;
        return true;
    }

    public String getQueueStatus() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");

        Node current = front;
        while (current != null) {
            sb.append(current.visitor.getId());
            if (current.visitor.isVIP()) {
                sb.append("(VIP)");
            }
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");

        return sb.toString();
    }
    public boolean isEmpty() {
        return front == null;
    }

    public int getSize() {
        return size;
    }
}