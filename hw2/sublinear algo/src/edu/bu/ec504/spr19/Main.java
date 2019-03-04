package edu.bu.ec504.spr19;

public class Main {

    public static void main(String[] args) {
        ThreadedVector<Double> example = new myThreadedVector<>();
        example.add(3.14);
        example.add(1.41);
        example.add(4.15);
        example.add(1.59);
        example.add(5.92);
        example.add(9.26);
        example.add(2.65);

        // print the data structure contents
        System.out.println(example);

        // print the elements, in order
        ThreadedVector<Double>.link theLink = example.getSmallest();
        try {
            while (theLink != null) {
                System.out.print(theLink.datum + " ");
                theLink = example.getNextLarger(theLink);
            }
        } catch (ThreadedVector.nullElementException ignored) {
        }
        System.out.println();

        System.out.println("Looking for 5.92: "+example.search(5.92));
        System.out.println("Looking for 5.93: "+example.search(5.93));

        System.out.println("Total number of getLink calls: "+example.getGetLinkCount());
    }
}
