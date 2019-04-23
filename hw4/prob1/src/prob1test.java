import java.util.ArrayList;

public class prob1test {
    public static void main(String[] args) {
        prob1Graph testGraph = new prob1Graph();
        testGraph.printGraph();
        System.out.println("add some vertices into the graph");
        testGraph.addVertex();
        testGraph.addVertex();
        testGraph.addVertex();
        testGraph.addVertex();
        testGraph.addVertex();
        testGraph.addVertex();
        testGraph.printGraph();
        System.out.println("add some edges into the graph");
        testGraph.addEdge(1,2,6);
        testGraph.addEdge(2,3,8);
        testGraph.addEdge(1,3,10);
        testGraph.addEdge(3,5,5);
        testGraph.addEdge(3,4,3);
        testGraph.addEdge(5,6,4);
        testGraph.addEdge(4,6,24);
        testGraph.printGraph();
        //testGraph.reduceEdge(1,2,3);
        //testGraph.printGraph();

        ArrayList<prob1Vertex> vs = testGraph.shortestPath(1,6);
        if(vs!=null) {
            System.out.print("the index of returned list of vertices:");
            for(int ii=0;ii<vs.size();ii++)
                System.out.print(vs.get(ii).index+"  ");
            System.out.print("\n");
        }


        System.out.println("modify the graph to test a graph containing a negative cycle");
        testGraph.reduceEdge(1,3,-20);
        testGraph.addEdge(3,1,1);
        testGraph.printGraph();
        vs = testGraph.shortestPath(1,6);

    }
}
