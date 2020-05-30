public class Main1Pass {
    public static void main(String[] args) {
        BTree<Integer> bt = new BTree();    System.out.println(bt.toString() + "\n");
        System.out.println("Inserting \n");
        bt.insert(1);  System.out.println("Inserting 1 \n" + bt.toString());
        bt.insert(2);  System.out.println("Inserting 2 \n" + bt.toString());
        bt.insert(3);  System.out.println("Inserting 3 \n" + bt.toString());
        bt.insert(4);  System.out.println("Inserting 4 \n" + bt.toString());
        bt.insert(5);  System.out.println("Inserting 5 \n" + bt.toString());
        bt.insert(6);  System.out.println("Inserting 6 \n" + bt.toString());
        bt.insert(71);  System.out.println("Inserting 71 \n" + bt.toString());
        bt.insert(8);  System.out.println("Inserting 8 \n" + bt.toString());
        bt.insert(9);  System.out.println("Inserting 9 \n" + bt.toString());
        bt.insert(10);  System.out.println("Inserting 10 \n" + bt.toString());
        bt.insert(11);  System.out.println("Inserting 11 \n" + bt.toString());
        bt.insert(12);  System.out.println("Inserting 12 \n" + bt.toString());
        bt.insert(13);  System.out.println("Inserting 13 \n" + bt.toString());

        System.out.println("Deleting \n");
        bt.delete(3);   System.out.println("Deleting 3 \n" + bt.toString());
        bt.delete(2);   System.out.println("Deleting 2 \n" + bt.toString());
        bt.delete(7);   System.out.println("Deleting 7 \n" + bt.toString());
        bt.delete(9);   System.out.println("Deleting 9 \n" + bt.toString());
        bt.delete(8);   System.out.println("Deleting 8 \n" + bt.toString());
        bt.delete(11);   System.out.println("Deleting 11 \n" + bt.toString());
        bt.delete(10);   System.out.println("Deleting 10 \n" + bt.toString());
        bt.delete(5);   System.out.println("Deleting 5 \n" + bt.toString());
        bt.delete(12);   System.out.println("Deleting 12 \n" + bt.toString());
        bt.delete(1);   System.out.println("Deleting 1 \n" + bt.toString());
        bt.delete(71);   System.out.println("Deleting 71 \n" + bt.toString());
        bt.delete(6);   System.out.println("Deleting 6 \n" + bt.toString());
        bt.delete(4);   System.out.println("Deleting 4 \n" + bt.toString());
        bt.delete(13);   System.out.println("Deleting 13 \n" + bt.toString());
    }
}
