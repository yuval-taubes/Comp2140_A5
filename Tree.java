import java.util.Stack;

public class Tree<E>
{
    private class Node {
        int[] keys;
        Object[] values;
        Node[] children;
        int numKeys;

        Node() {
            keys = new int[3];        // allow overflow before splitting
            values = new Object[3];
            //IDE was being stupid so I had to create a raw array
            children = (Node[]) new Tree.Node[4];    // max 3 keys = 4 children
            numKeys = 0;
        }

        boolean isLeaf() {
            return children[0] == null;  // leaf has no children
        }
    }

    private Node root;

    public Tree() {
        root = null;
    }

    public E search(int key)
    {
        Node curr = root;

        while (curr != null) {
            // check first key
            if (key == curr.keys[0]) {
                // I can cast to e because only e types values stored here
                return (E) curr.values[0];
            }

            if (curr.numKeys == 1) {
                // only one key, go left or right
                if (key < curr.keys[0]) {
                    curr = curr.children[0];
                } else {
                    curr = curr.children[1];
                }
            } else {
                // two keys in this node
                if (key == curr.keys[1]) {
                    return (E) curr.values[1];
                } else if (key < curr.keys[0]) {
                    curr = curr.children[0];
                } else if (key < curr.keys[1]) {
                    curr = curr.children[1];
                } else {
                    curr = curr.children[2];
                }
            }
        }

        return null;  // not found
    }

    public void insert(int key, E value)
    {
        if (root == null) {
            root = new Node();
            root.keys[0] = key;
            root.values[0] = value;
            root.numKeys = 1;
            return;
        }

        Stack<Node> path = new Stack<>();  // stack to remember parent nodes
        Node curr = root;

        // walk down to the leaf where insertion belongs
        while (!curr.isLeaf()) {
            path.push(curr);

            if (key < curr.keys[0]) {
                curr = curr.children[0];
            } else if (curr.numKeys == 1 || key < curr.keys[1]) {
                curr = curr.children[1];
            } else {
                curr = curr.children[2];
            }
        }

        // insert into the leaf
        insertIntoNode(curr, key, value, null, null);

        // fix overflow by splitting up the tree
        while (curr.numKeys == 3) {
            Node right = new Node();  // new sibling node

            int midKey = curr.keys[1];
            Object midValue = curr.values[1];

            // right node gets the largest key
            right.keys[0] = curr.keys[2];
            right.values[0] = curr.values[2];
            right.numKeys = 1;

            // move children over if not a leaf
            if (!curr.isLeaf()) {
                right.children[0] = curr.children[2];
                right.children[1] = curr.children[3];
            }

            // left node keeps the smallest key
            curr.numKeys = 1;

            if (path.isEmpty()) {
                // need a new root
                Node newRoot = new Node();
                newRoot.keys[0] = midKey;
                newRoot.values[0] = midValue;
                newRoot.numKeys = 1;

                newRoot.children[0] = curr;
                newRoot.children[1] = right;

                root = newRoot;
                return;
            }

            // push middle key up to parent
            Node parent = path.pop();
            insertIntoNode(parent, midKey, (E) midValue, curr, right);
            curr = parent;
        }
    }

    private void insertIntoNode(Node node, int key, E value, Node leftChild, Node rightChild)
    {
        int i = node.numKeys - 1;

        // shift keys right to make room
        while (i >= 0 && key < node.keys[i]) {
            node.keys[i + 1] = node.keys[i];
            node.values[i + 1] = node.values[i];
            i--;
        }

        // insert new key
        node.keys[i + 1] = key;
        node.values[i + 1] = value;
        node.numKeys++;

        // shift children if we're splitting (rightChild != null means we're promoting)
        if (rightChild != null) {
            for (int j = node.numKeys; j > i + 2; j--) {
                node.children[j] = node.children[j - 1];
            }

            node.children[i + 1] = leftChild;
            node.children[i + 2] = rightChild;
        }
    }

    public void print()
    {
        if (root != null) {
            printRecursive(root, 0);
        }
    }

    private void printRecursive(Node curr, int currDepth)
    {
        if (curr == null) return;

        // leftmost subtree
        printRecursive(curr.children[0], currDepth + 1);

        // first key (with indents)
        for (int i = 0; i < currDepth; i++) {
            System.out.print("    ");
        }
        System.out.println(curr.keys[0]);

        // middle subtree
        printRecursive(curr.children[1], currDepth + 1);

        // second key and right subtree (if exists)
        if (curr.numKeys == 2) {
            for (int i = 0; i < currDepth; i++) {
                System.out.print("    ");
            }
            System.out.println(curr.keys[1]);

            printRecursive(curr.children[2], currDepth + 1);
        }
    }
}