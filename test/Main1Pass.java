public class Main1Pass {
    public static void main(String[] args) {
        BTree<Integer> bt = new BTree();
        bt.insert(1);  System.out.println(bt.toString());
        bt.insert(2);  System.out.println(bt.toString());
        bt.insert(3);  System.out.println(bt.toString());
        bt.insert(4);  System.out.println(bt.toString());
        bt.insert(5);  System.out.println(bt.toString());
        bt.insert(6);  System.out.println(bt.toString());
        bt.insert(71);  System.out.println(bt.toString());
        bt.insert(8);  System.out.println(bt.toString());
        bt.insert(9);  System.out.println(bt.toString());
        bt.insert(10);  System.out.println(bt.toString());
        bt.insert(11);  System.out.println(bt.toString());
        bt.insert(12);  System.out.println(bt.toString());
        bt.insert(13);  System.out.println(bt.toString());

        bt.remove(3);   System.out.println(bt.toString());
        bt.remove(2);   System.out.println(bt.toString());
        bt.remove(7);   System.out.println(bt.toString());
        bt.remove(9);   System.out.println(bt.toString());
        bt.remove(8);   System.out.println(bt.toString());
        bt.remove(11);   System.out.println(bt.toString());
        bt.remove(10);   System.out.println(bt.toString());
        bt.remove(12);   System.out.println(bt.toString());
        bt.remove(1);   System.out.println(bt.toString());
        bt.remove(71);   System.out.println(bt.toString());
        bt.remove(6);   System.out.println(bt.toString());
        bt.remove(4);   System.out.println(bt.toString());
        bt.remove(13);   System.out.println(bt.toString());
    }
}
