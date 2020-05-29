import org.junit.*;

public class Test {

    BTree<Integer> tree = createBTee();

    @BeforeClass
    public static BTree<Integer> createBTee(){
        BTree<Integer> tree = new BTree<>();
        tree.insert2pass(10);
        tree.insert2pass(3);
        tree.insert2pass(17);
        tree.insert2pass(7);
        tree.insert2pass(12);
        tree.insert2pass(2);
        tree.insert2pass(5);
        tree.insert2pass(1);
        tree.insert2pass(24);
        tree.insert2pass(9);
        tree.insert2pass(-2);
        tree.insert2pass(11);
        tree.insert2pass(15);
        tree.insert2pass(60);
        tree.insert2pass(35);

        return tree;
    }
}
