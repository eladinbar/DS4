import org.junit.*;

public class Test {

    private BTree<Integer> tree;

    @Before
    public void createBTee(){
        tree = new BTree<>(5);
        for (int i = 0; i <= 200 ; i++) {
            tree.add((int) (10000*Math.random()));
        }
    }

    @org.junit.Test
    public void printTree(){
        System.out.println(tree.toString());
        int i = 5;
        System.out.println(i);
        int j = -i;
        System.out.println(j);

    }



}
