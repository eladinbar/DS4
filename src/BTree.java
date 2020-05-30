import java.util.Arrays;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("unchecked")
public class BTree<T extends Comparable<T>> {

    // Default to 2-3 Tree
    private int minKeySize = 1;
    private int minChildrenSize = minKeySize + 1; // 2
    private int maxKeySize = (2 * minKeySize) + 1; // 2 //Edit to reflect actual 't' value as shown in class - according to the forum t=2 by default
    private int maxChildrenSize = maxKeySize + 1; // 3

    private Node<T> root = null;
    private int size = 0;

    public Node<T> getRoot() {
        return root;
    }

    /**
     * Constructor for B-Tree which defaults to a 2-3 B-Tree.
     */
    public BTree() {
    }

    /**
     * Constructor for B-Tree of ordered parameter. Order here means minimum
     * number of keys in a non-root node.
     *
     * @param order of the B-Tree.
     */
    public BTree(int order) {
        this.minKeySize = order - 1; //Edit to reflect actual 't' value as shown in class
        this.minChildrenSize = minKeySize + 1;
        this.maxKeySize = (2 * order) - 1;
        this.maxChildrenSize = maxKeySize + 1;
    }

    //Task 2.1
    public boolean insert(T value) {
        if (root == null) {
            root = new Node<>(null, maxKeySize, maxChildrenSize);
            root.addKey(value);
        } else {
            Node<T> node = root;
            while (node != null) {
                if (node.numberOfKeys() == maxKeySize) //Need to split up
                    node = split(node);
                if (node.numberOfChildren() == 0) {
                    // A-OK
                    node.addKey(value);
                    break;
                }
                // Navigate

                // Lesser or equal
                T lesser = node.getKey(0);
                if (value.compareTo(lesser) <= 0) {
                    node = node.getChild(0);
                    continue;
                }

                // Greater
                int last = node.numberOfKeys() - 1;
                T greater = node.getKey(last);
                if (value.compareTo(greater) > 0) {
                    node = node.getChild(node.numberOfChildren() - 1); //Original - node.numberOfKeys()
                    continue;
                }

                // Search internal nodes
                for (int i = 1; i < node.numberOfKeys(); i++) {
                    T lower = node.getKey(i - 1);
                    T upper = node.getKey(i);
                    if (value.compareTo(lower) > 0 && value.compareTo(upper) <= 0) {
                        node = node.getChild(i);
                        break;
                    }
                }
            }
        }

        size++;

        return true;
    }

    public T delete(T value) {
//        T removed = null;
//        Node<T> node = this.getNode(value);
//        removed = remove(value, node);
//        return removed;

        //

        Node<T> node = this.getNode(value);
        if (node == null)
            return null;

        int valueIndex = node.indexOf(value);
        T removed = node.removeKey(value); //Move down?
        if (node.numberOfChildren() == 0) {
            // leaf node
            if (node.parent != null && node.numberOfKeys() == minKeySize) {
                this.combined(node);
            } else if (node.parent == null && node.numberOfKeys() == 0) {
                // Removing root node with no keys or children
                root = null;
            }
        } else {
            // internal node
            Node<T> lesser = node.getChild(valueIndex);
            Node<T> greatest = this.getGreatestNode(lesser);
            T replaceValue = this.removeGreatestValue(greatest);
            node.addKey(replaceValue);
            if (greatest.parent != null && greatest.numberOfKeys() < minKeySize) {
                this.combined(greatest);
            }
            if (greatest.numberOfChildren() > maxChildrenSize) {
                this.split(greatest);
            }
        }

        size--;

        return removed;
    }

    //Task 2.2
    public boolean insert2pass(T value) {
        if (root == null) {
            root = new Node<T>(null, maxKeySize, maxChildrenSize);
            root.addKey(value);
        } else {
            Queue<Node<T>> queueNodeToSplit = new ConcurrentLinkedQueue<>();
            Node<T> node = root;
            while (node != null) {
                if (node.numberOfKeys() < maxKeySize)
                    queueNodeToSplit.clear();
                if (node.numberOfChildren() == 0) {
                    //reached a leaf
                    Node<T> parent = node.parent;
                    if (parent != null) {
                        //inserting at a leaf
                        if (node.numberOfKeys() >= maxKeySize) {
                            //splitting from the first node that need to be split until the leaf
                            queueNodeToSplit.add(node);
                            parent = twoPassSplit(queueNodeToSplit);
                            //adding value to the correct half of the split node
                            parent.getChild(findChildIndexToInsert(parent, value)).addKey(value);
                            break;
                        } else {
                            node.addKey(value);
                            break;
                        }
                    } else {
                        //inserting at root
                        if (node.numberOfKeys() >= maxKeySize) {
                            //splitting root
                            queueNodeToSplit.add(node);
                            twoPassSplit(queueNodeToSplit);
                            if (value.compareTo(root.getKey(0)) <= 0) {
                                root.getChild(0).addKey(value);
                            } else {
                                root.getChild(1).addKey(value);
                            }
                            break;
                        } else {
                            //not splitting root
                            node.addKey(value);
                            break;
                        }
                    }
                }

                //searching the leaf to insert value
                //at the left most route
                T smallestInNode = node.getKey(0);
                if (value.compareTo(smallestInNode) <= 0) {
                    if (node.numberOfKeys() >= maxKeySize)
                        queueNodeToSplit.add(node);
                    node = node.getChild(0);
                    continue;
                }

                //at the right most path
                T largestInNode = node.getKey(node.numberOfKeys() - 1);
                if (value.compareTo(largestInNode) > 0) {
                    if (node.numberOfKeys() >= maxKeySize)
                        queueNodeToSplit.add(node);
                    node = node.getChild(node.numberOfChildren() - 1);
                    continue;
                }

                //in between keys
                for (int i = 1; i < node.numberOfKeys(); i++) {
                    T prev = node.getKey(i - 1);
                    T next = node.getKey(i);
                    if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
                        if (node.numberOfKeys() >= maxKeySize)
                            queueNodeToSplit.add(node);
                        node = node.getChild(i);
                        break;
                    }
                }
            }
        }
        size++;
        return true;
    }

    //splitting all the nodes in the queue according to 2passInsert logic.
    //returns the new parent of the last node that split
    private Node<T> twoPassSplit(Queue<Node<T>> queuedNodesToSplit) {
        Node<T> toSplit;
        Node<T> parentOfLastSplit = root;
        while (!queuedNodesToSplit.isEmpty()) {
            toSplit = queuedNodesToSplit.remove();
            parentOfLastSplit = split(toSplit);
        }
        return parentOfLastSplit;
    }

    private int findChildIndexToInsert(Node<T> node, T value){
        //left most child
        T smallestInNode = node.getKey(0);
        T largestInNode = node.getKey(node.numberOfKeys() - 1);
        if (value.compareTo(smallestInNode) <= 0) {
            return 0;
        }
        //right most child
        else if (value.compareTo(largestInNode) > 0) {
            return node.numberOfChildren() - 1;
        }
        //in between
        else {
            for (int i = 1; i < node.numberOfKeys(); i++) {
                T prev = node.getKey(i - 1);
                T next = node.getKey(i);
                if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
                    return i;
                }
            }
            return -1;
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean add(T value) {
        if (root == null) {
            root = new Node<T>(null, maxKeySize, maxChildrenSize);
            root.addKey(value);
        } else {
            Node<T> node = root;
            while (node != null) {
                if (node.numberOfChildren() == 0) {
                    node.addKey(value);
                    if (node.numberOfKeys() <= maxKeySize) {
                        // A-OK
                        break;
                    }
                    // Need to split up
                    split(node);
                    break;
                }
                // Navigate

                // Lesser or equal
                T lesser = node.getKey(0);
                if (value.compareTo(lesser) <= 0) {
                    node = node.getChild(0);
                    continue;
                }

                // Greater
                int numberOfKeys = node.numberOfKeys();
                int last = numberOfKeys - 1;
                T greater = node.getKey(last);
                if (value.compareTo(greater) > 0) {
                    node = node.getChild(numberOfKeys);
                    continue;
                }

                // Search internal nodes
                for (int i = 1; i < node.numberOfKeys(); i++) {
                    T prev = node.getKey(i - 1);
                    T next = node.getKey(i);
                    if (value.compareTo(prev) > 0 && value.compareTo(next) <= 0) {
                        node = node.getChild(i);
                        break;
                    }
                }

            }
        }

        size++;

        return true;
    }

    /**
     * The node's key size is greater than maxKeySize, split down the middle.
     *
     * @param nodeToSplit to split.
     * @returns the parent of the split node
     */
    private Node<T> split(Node<T> nodeToSplit) {
        Node<T> node = nodeToSplit;                       /**Redundant declaration? No setting actions are performed.*/
        int numberOfKeys = node.numberOfKeys();
        //finding the middle key to move to the parent node
        int middleKey = numberOfKeys / 2;
        T middleKeyValue = node.getKey(middleKey);

        //creating the new node that needs to be to the left of the middleKey
        Node<T> leftHalf = new Node<>(null, maxKeySize, maxChildrenSize);
        for (int i = 0; i < middleKey; i++) {
            leftHalf.addKey(node.getKey(i));
        }
        //moving the children to the leftHalf
        if (node.numberOfChildren() > 0) {
            for (int i = 0; i <= middleKey; i++) {
                Node<T> child = node.getChild(i);
                leftHalf.addChild(child);
            }
        }

        //creating the new node that needs to be to the right of the middleKey
        Node<T> rightHalf = new Node<>(null, maxKeySize, maxChildrenSize);
        for (int i = middleKey + 1; i < numberOfKeys; i++) {
            rightHalf.addKey(node.getKey(i));
        }
        //moving the children to the rightHalf
        if (node.numberOfChildren() > 0) {
            for (int i = middleKey + 1; i < node.numberOfChildren(); i++) {
                Node<T> child = node.getChild(i);
                rightHalf.addChild(child);
            }
        }

        //splitting the root
        if (node.parent == null) {
            Node<T> newRoot = new Node<>(null, maxKeySize, maxChildrenSize);
            newRoot.addKey(middleKeyValue);
            newRoot.addChild(leftHalf);
            newRoot.addChild(rightHalf);
            root = newRoot;
            return root;
        }
        //splitting an internal node
        else {
            Node<T> parent = node.parent;
            parent.addKey(middleKeyValue);
            parent.addChild(leftHalf);
            parent.addChild(rightHalf);
            parent.removeChild(node); /**Remove needs to occur first? Child addition is performed beyond maxChildrenSize? */
            return parent;
        }
    }

    /**
     * {@inheritDoc}
     */
    public T remove(T value) {
        T removed = null;
        Node<T> node = this.getNode(value);
        removed = remove(value, node);
        return removed;
    }

    /**
     * Remove the value from the Node and check invariants
     *
     * @param value T to remove from the tree
     * @param node  Node to remove value from
     * @return True if value was removed from the tree.
     */
    private T remove(T value, Node<T> node) {
        if (node == null) return null;

        T removed = null; //Redundant
        int index = node.indexOf(value);
        removed = node.removeKey(value);
        if (node.numberOfChildren() == 0) {
            // leaf node
            if (node.parent != null && node.numberOfKeys() < minKeySize) {
                this.combined(node);
            } else if (node.parent == null && node.numberOfKeys() == 0) {
                // Removing root node with no keys or children
                root = null;
            }
        } else {
            // internal node
            Node<T> lesser = node.getChild(index);
            Node<T> greatest = this.getGreatestNode(lesser);
            T replaceValue = this.removeGreatestValue(greatest);
            node.addKey(replaceValue);
            if (greatest.parent != null && greatest.numberOfKeys() < minKeySize) {
                this.combined(greatest);
            }
            if (greatest.numberOfChildren() > maxChildrenSize) {
                this.split(greatest);
            }
        }

        size--;

        return removed;
    }

    /**
     * Remove greatest valued key from node.
     *
     * @param node to remove greatest value from.
     * @return value removed;
     */
    private T removeGreatestValue(Node<T> node) {
        T value = null;
        if (node.numberOfKeys() > 0) {
            value = node.removeKey(node.numberOfKeys() - 1);
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(T value) {
        Node<T> node = getNode(value);
        return (node != null);
    }

    /**
     * Get the node with value.
     *
     * @param value to find in the tree.
     * @return Node<T> with value.
     */
    private Node<T> getNode(T value) {
        Node<T> node = root;
        while (node != null) {
            T lesser = node.getKey(0);
            if (value.compareTo(lesser) < 0) {
                if (node.numberOfChildren() > 0)
                    node = node.getChild(0);
                else
                    node = null;
                continue;
            }

            int numberOfKeys = node.numberOfKeys();
            int last = numberOfKeys - 1;
            T greater = node.getKey(last);
            if (value.compareTo(greater) > 0) {
                if (node.numberOfChildren() > numberOfKeys)
                    node = node.getChild(numberOfKeys);
                else
                    node = null;
                continue;
            }

            for (int i = 0; i < numberOfKeys; i++) {
                T currentValue = node.getKey(i);
                if (currentValue.compareTo(value) == 0) {
                    return node;
                }

                int next = i + 1;
                if (next <= last) {
                    T nextValue = node.getKey(next);
                    if (currentValue.compareTo(value) < 0 && nextValue.compareTo(value) > 0) {
                        if (next < node.numberOfChildren()) {
                            node = node.getChild(next);
                            break;
                        }
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the greatest valued child from node.
     *
     * @param nodeToGet child with the greatest value.
     * @return Node<T> child with greatest value.
     */
    private Node<T> getGreatestNode(Node<T> nodeToGet) {
        Node<T> node = nodeToGet;
        while (node.numberOfChildren() > 0) {
            node = node.getChild(node.numberOfChildren() - 1);
        }
        return node;
    }

    /**
     * Combined children keys with parent when size is less than minKeySize.
     *
     * @param node with children to combined.
     * @return True if combined successfully.
     */
    private boolean combined(Node<T> node) { /**No return false statement - perhaps change return value to T? */
        Node<T> parent = node.parent;
        int borrowerNodeIndex = parent.indexOf(node);
        int leftBrotherIndex = borrowerNodeIndex - 1;
        int rightBrotherIndex = borrowerNodeIndex + 1;

        Node<T> leftBrother = parent.getChild(leftBrotherIndex);
        Node<T> rightBrother = parent.getChild(rightBrotherIndex);

        //try to borrow key from the left brother
        if (leftBrotherIndex >= 0 && leftBrother.numberOfKeys() > minKeySize) {
            T leftBrotherMaxKey = leftBrother.getKey(leftBrother.keysSize - 1);
            Node<T> leftBrotherRightChild = leftBrother.getChild(leftBrother.childrenSize - 1);
            int separatingKeyIndex = leftBrotherIndex;
            T parentKeyToMove = parent.removeKey(separatingKeyIndex);
            parent.addKey(leftBrotherMaxKey);
            node.addKey(parentKeyToMove);
            node.addChild(leftBrotherRightChild);
        }
        //try to borrow key from the right brother
        else if (rightBrotherIndex < parent.numberOfChildren() && rightBrother.numberOfKeys() > minKeySize) {
            T rightBrotherMinKey = rightBrother.getKey(0);
            Node<T> rightBrotherLeftChild = rightBrother.getChild(0);
            int separatingKeyIndex = borrowerNodeIndex;
            T parentKeyToMove = parent.removeKey(separatingKeyIndex);
            parent.addKey(rightBrotherMinKey);
            node.addKey(parentKeyToMove);
            node.addChild(rightBrotherLeftChild);
        }
        //can't borrow key from brothers, merge nodes
        else {
            /**productNode never seems to be assigned a parent nor does the parent remove its old children.
             * Change parent in constructor from null to 'parent'? */
            Node<T> productNode = new Node<>(null, maxKeySize, maxChildrenSize); //creating an empty node for merging
            //try to merge with left brother if it exists
            if (leftBrotherIndex >= 0) {
                int borrowedKeyIndex = leftBrotherIndex;
                productNode.addKey(parent.removeKey(borrowedKeyIndex));
                //adding the left brother's keys
                for (int i = 0; i < leftBrother.numberOfKeys(); i++) {
                    T key = leftBrother.getKey(i);
                    productNode.addKey(key);
                }
                // if leftBrother has children, move them to productNode
                if (leftBrother.numberOfChildren() > 0) {
                    for (int i = 0; i < leftBrother.numberOfChildren(); i++) {
                        Node<T> child = leftBrother.getChild(i);
                        productNode.addChild(child);
                    }
                }
                //adding the node's keys to the product node
                for (int i = 0; i < node.numberOfKeys(); i++) {
                    T key = node.getKey(i);
                    productNode.addKey(key);
                }
                //if node has children, move them to productNode
                if (node.numberOfChildren() > 0) {
                    for (int i = 0; i < node.numberOfChildren(); i++) {
                        Node<T> child = node.getChild(i);
                        productNode.addChild(child);
                    }
                }
            }
            //else merge with the right brother
            else {
                int borrowedKeyIndex = borrowerNodeIndex; /**RightBrotherIndex? */
                productNode.addKey(parent.removeKey(borrowedKeyIndex));
                //adding the right brother's keys
                for (int i = 0; i < rightBrother.numberOfChildren(); i++) { /**rightBrother.numberOfKeys()? */
                    T key = rightBrother.getKey(i);
                    productNode.addKey(key);
                }
                //if rightBrother has children, move them to productNode
                if (rightBrother.numberOfChildren() > 0) {
                    for (int i = 0; i < rightBrother.numberOfChildren(); i++) {
                        Node<T> child = rightBrother.getChild(i);
                        productNode.addChild(child);
                    }
                }

                //adding the node's keys to the product node
                for (int i = 0; i < node.numberOfKeys(); i++) {
                    T key = node.getKey(i);
                    productNode.addKey(key);
                }
                //if node has children, move them to productNode
                if (node.numberOfChildren() > 0) {
                    for (int i = 0; i < node.numberOfChildren(); i++) {
                        Node<T> child = node.getChild(i);
                        productNode.addChild(child);
                    }
                }
            }
        }
        return true;
    }

    /**
     * Get the index of previous key in node.
     *
     * @param node  to find the previous key in.
     * @param value to find a previous value for.
     * @return index of previous key or -1 if not found.
     */
    private int getIndexOfPreviousValue(Node<T> node, T value) {
        for (int i = 1; i < node.numberOfKeys(); i++) {
            T t = node.getKey(i);
            if (t.compareTo(value) >= 0)
                return i - 1;
        }
        return node.numberOfKeys() - 1;
        //not returning -1 if there is no key
    }

    /**
     * Get the index of next key in node.
     *
     * @param node  to find the next key in.
     * @param value to find a next value for.
     * @return index of next key or -1 if not found.
     */
    private int getIndexOfNextValue(Node<T> node, T value) {
        for (int i = 0; i < node.numberOfKeys(); i++) {
            T t = node.getKey(i);
            if (t.compareTo(value) >= 0)
                return i;
        }
        return node.numberOfKeys() - 1;
        //not returning -1 if there is no key
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return size;
    }

    /**
     * {@inheritDoc}
     */
    public boolean validate() {
        if (root == null) return true;
        return validateNode(root);
    }

    /**
     * Validate the node according to the B-Tree invariants.
     *
     * @param node to validate.
     * @return True if valid.
     */
    private boolean validateNode(Node<T> node) {
        int keySize = node.numberOfKeys();
        if (keySize > 1) {
            // Make sure the keys are sorted
            for (int i = 1; i < keySize; i++) {
                T p = node.getKey(i - 1);
                T n = node.getKey(i);
                if (p.compareTo(n) > 0)
                    return false;
            }
        }
        int childrenSize = node.numberOfChildren();
        if (node.parent == null) {
            // root
            if (keySize > maxKeySize) {
                // check max key size. root does not have a min key size
                return false;
            } else if (childrenSize == 0) {
                // if root, no children, and keys are valid
                return true;
            } else if (childrenSize < 2) {
                // root should have zero or at least two children
                return false;
            } else if (childrenSize > maxChildrenSize) {
                return false;
            }
        } else {
            // non-root
            if (keySize < minKeySize) {
                return false;
            } else if (keySize > maxKeySize) {
                return false;
            } else if (childrenSize == 0) {
                return true;
            } else if (keySize != (childrenSize - 1)) {
                // If there are chilren, there should be one more child then
                // keys
                return false;
            } else if (childrenSize < minChildrenSize) {
                return false;
            } else if (childrenSize > maxChildrenSize) {
                return false;
            }
        }

        Node<T> first = node.getChild(0);
        // The first child's last key should be less than the node's first key
        if (first.getKey(first.numberOfKeys() - 1).compareTo(node.getKey(0)) > 0)
            return false;

        Node<T> last = node.getChild(node.numberOfChildren() - 1);
        // The last child's first key should be greater than the node's last key
        if (last.getKey(0).compareTo(node.getKey(node.numberOfKeys() - 1)) < 0)
            return false;

        // Check that each node's first and last key holds it's invariance
        for (int i = 1; i < node.numberOfKeys(); i++) {
            T p = node.getKey(i - 1);
            T n = node.getKey(i);
            Node<T> c = node.getChild(i);
            if (p.compareTo(c.getKey(0)) > 0)
                return false;
            if (n.compareTo(c.getKey(c.numberOfKeys() - 1)) < 0)
                return false;
        }

        for (int i = 0; i < node.childrenSize; i++) { //can make the validation on the entire tree or on an entire sub tree and not on a specific node
            Node<T> c = node.getChild(i);
            boolean valid = this.validateNode(c);
            if (!valid)
                return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TreePrinter.getString(this);
    }


    private static class Node<T extends Comparable<T>> {

        private T[] keys = null;
        private int keysSize = 0;
        private Node<T>[] children = null;
        private int childrenSize = 0;
        private Comparator<Node<T>> comparator = new Comparator<Node<T>>() {
            public int compare(Node<T> arg0, Node<T> arg1) {
                return arg0.getKey(0).compareTo(arg1.getKey(0));
            }
        };

        protected Node<T> parent = null;

        private Node(Node<T> parent, int maxKeySize, int maxChildrenSize) {
            this.parent = parent;
            this.keys = (T[]) new Comparable[maxKeySize + 1];
            this.keysSize = 0;
            this.children = new Node[maxChildrenSize + 1];
            this.childrenSize = 0;
        }

        private T getKey(int index) {
            return keys[index];
        }

        private int indexOf(T value) {
            for (int i = 0; i < keysSize; i++) {
                if (keys[i].equals(value)) return i; //CompareTo
            }
            return -1;
        }

        private void addKey(T value) {
            keys[keysSize++] = value;
            Arrays.sort(keys, 0, keysSize);
        }

        private T removeKey(T value) {
            T removed = null;
            boolean found = false;
            if (keysSize == 0) return null;
            for (int i = 0; i < keysSize; i++) {
                if (keys[i].equals(value)) { //compareTo
                    found = true;
                    removed = keys[i];
                } else if (found) {
                    // shift the rest of the keys down
                    keys[i - 1] = keys[i];
                }
            }
            if (found) {
                keysSize--;
                keys[keysSize] = null;
            }
            return removed;
        }

        private T removeKey(int index) {
            if (index >= keysSize || index < 0)
                return null;
            T value = keys[index];
            for (int i = index + 1; i < keysSize; i++) {
                // shift the rest of the keys down
                keys[i - 1] = keys[i];
            }
            keysSize--;
            keys[keysSize] = null;
            return value;
        }

        private int numberOfKeys() {
            return keysSize;
        }

        private Node<T> getChild(int index) {
            if (index >= childrenSize || index < 0)
                return null;
            return children[index];
        }

        private int indexOf(Node<T> child) {
            for (int i = 0; i < childrenSize; i++) {
                if (children[i].equals(child))
                    return i;
            }
            return -1;
        }

        private boolean addChild(Node<T> child) {
            child.parent = this;
            children[childrenSize++] = child;
            Arrays.sort(children, 0, childrenSize, comparator);
            return true;
        }

        private boolean removeChild(Node<T> child) {
            boolean found = false;
            if (childrenSize == 0)
                return found;
            for (int i = 0; i < childrenSize; i++) {
                if (children[i].equals(child)) {
                    found = true;
                } else if (found) {
                    // shift the rest of the keys down
                    children[i - 1] = children[i];
                }
            }
            if (found) {
                childrenSize--;
                children[childrenSize] = null;
            }
            return found;
        }

        private Node<T> removeChild(int index) {
            if (index >= childrenSize)
                return null;
            Node<T> value = children[index];
            children[index] = null;
            for (int i = index + 1; i < childrenSize; i++) {
                // shift the rest of the keys down
                children[i - 1] = children[i];
            }
            childrenSize--;
            children[childrenSize] = null;
            return value;
        }

        private int numberOfChildren() {
            return childrenSize;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();

            builder.append("keys=[");
            for (int i = 0; i < numberOfKeys(); i++) {
                T value = getKey(i);
                builder.append(value);
                if (i < numberOfKeys() - 1)
                    builder.append(", ");
            }
            builder.append("]\n");

            if (parent != null) {
                builder.append("parent=[");
                for (int i = 0; i < parent.numberOfKeys(); i++) {
                    T value = parent.getKey(i);
                    builder.append(value);
                    if (i < parent.numberOfKeys() - 1)
                        builder.append(", ");
                }
                builder.append("]\n");
            }

            if (children != null) {
                builder.append("keySize=").append(numberOfKeys()).append(" children=").append(numberOfChildren()).append("\n");
            }

            return builder.toString();
        }
    }

    private static class TreePrinter {

        public static <T extends Comparable<T>> String getString(BTree<T> tree) {
            if (tree.root == null) return "Tree has no nodes.";
            return getString(tree.root, "", true);
        }

        private static <T extends Comparable<T>> String getString(Node<T> node, String prefix, boolean isTail) {
            StringBuilder builder = new StringBuilder();

            builder.append(prefix).append((isTail ? "└── " : "├── "));
            for (int i = 0; i < node.numberOfKeys(); i++) {
                T value = node.getKey(i);
                builder.append(value);
                if (i < node.numberOfKeys() - 1)
                    builder.append(", ");
            }
            builder.append("\n");

            if (node.children != null) {
                for (int i = 0; i < node.numberOfChildren() - 1; i++) {
                    Node<T> obj = node.getChild(i);
                    builder.append(getString(obj, prefix + (isTail ? "    " : "│   "), false));
                }
                if (node.numberOfChildren() >= 1) {
                    Node<T> obj = node.getChild(node.numberOfChildren() - 1);
                    builder.append(getString(obj, prefix + (isTail ? "    " : "│   "), true));
                }
            }

            return builder.toString();
        }
    }

}