package structures;

public class AVL {
    private AVLNode root;
    private int size;

    private class AVLNode {
        Visitor visitor;
        AVLNode left;
        AVLNode right;
        int height;

        AVLNode(Visitor visitor) {
            this.visitor = visitor;
            this.height = 1;
        }
    }

    public AVL() {
        root = null;
        size = 0;
    }

    public void insert(Visitor visitor) {
        root = insert(root, visitor);
        size++;
    }

    private AVLNode insert(AVLNode node, Visitor visitor) {
        if (node == null) {
            return new AVLNode(visitor);
        }

        if (visitor.getId() < node.visitor.getId()) {
            node.left = insert(node.left, visitor);
        } else if (visitor.getId() > node.visitor.getId()) {
            node.right = insert(node.right, visitor);
        } else {
            size--;
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // چهار حالت چرخش
        //LL
        if (balance > 1 && getBalance(node.left)>=0) {
            return rightRotate(node);
        }
        //RR
        if (balance < -1 && getBalance(node.right)<=0) {
            return leftRotate(node);
        }
        //LR
        if (balance > 1 && getBalance(node.left)<0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        //RL
        if (balance < -1 && getBalance(node.right)>0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }


    public Visitor search(int id) {
        return search(root, id);
    }

    private Visitor search(AVLNode node, int id) {
        if (node == null || node.visitor.getId() == id) {
            return node == null ? null : node.visitor;
        }

        if (id < node.visitor.getId()) {
            return search(node.left, id);
        }

        return search(node.right, id);
    }
    public void delete(int id) {
        root = delete(root, id);
        size--;
    }

    private AVLNode delete(AVLNode node, int id) {
        if (node == null) return null;

        if (id < node.visitor.getId()) {
            node.left = delete(node.left, id);
        } else if (id > node.visitor.getId()) {
            node.right = delete(node.right, id);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left == null) ? node.right : node.left;
            } else {
                AVLNode minNode = findMin(node.right);
                node.visitor = minNode.visitor;
                node.right = delete(node.right, minNode.visitor.getId());
            }
        }

        if (node == null) return null;

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // بالانس کردن
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLNode findMin(AVLNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private AVLNode rightRotate(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private AVLNode leftRotate(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }
    public int size() {
        return size;
    }
    public boolean isEmpty() {
        return root == null;
    }


}
