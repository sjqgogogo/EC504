import java.util.ArrayList;
import java.util.LinkedList;

public class prob1Graph {

    ArrayList<prob1Vertex> verArray = new ArrayList<prob1Vertex>();

    private int vertexNum = 0;
    private int edgeNum = 0;
    private int[] distance;
    private int[] preNode;
    public static final int INF = 10000;
    public static final int NIL = -1;
    public prob1Graph() {
        verArray.add(new prob1Vertex());
    }

    public void printGraph() {
        if(verArray.size() == 1) {
            System.out.println("no graph yet");
            return;
        }
        System.out.println("start printing the graph");
        System.out.println("total vertex number: "+vertexNum);
        System.out.println("total edge number: "+edgeNum);
        System.out.println("(1,2,3) means that there is an edge from 1 to 2 with the weight 3");
        for(int ii=1;ii<verArray.size();ii++) {
            if(verArray.get(ii).next==null) {
                System.out.println("vertex "+ii+": doesn't have any edge");
                continue;
            }
            prob1Vertex v = verArray.get(ii).next;
            System.out.print("vertex "+ii+": ");
            while(v!=null) {
                System.out.print("("+ii+","+v.index+","+v.weight+") ");
                v = v.next;
            }
            System.out.print("\n");
        }
        System.out.println("end printing\n\n");
    }


    public void addVertex() {
        prob1Vertex v = new prob1Vertex();
        v.index = verArray.size();
        verArray.add(v);
        vertexNum++;
        //System.out.println("successfully add a vertex");
    }
    public boolean addEdge(int i,int j, int w) {
        if(this.vertexNum<i||this.vertexNum<j) {
            System.out.println("not enough vertices\npresent vertex number: "+this.vertexNum);
            return false;
        }
        if(i==j) {
            System.out.println("can't add an edge between same vertices");
            return false;
        }
        prob1Vertex v = this.verArray.get(i);
        while(v.next!=null) {
            if(v.next.index>j)
                break;
            if(v.next.index==j) {
                System.out.println("edge already exists");
                return false;
            }
            v = v.next;
        }
        prob1Vertex vNew = new prob1Vertex();
        vNew.index = j;
        vNew.next = v.next;
        vNew.weight = w;
        v.next = vNew;
        edgeNum++;
        //System.out.println("successfully add an edge between vertex "+i+" and "+j+" with weight "+w);
        return true;
    }
    public boolean reduceEdge(int i, int j, int w) {          //reduces the weight of the edge from the i -th vertex
                                                              // to the j -th vertex to the (lower) weight w .
        if(i>=verArray.size()||j>=verArray.size()) {
            System.out.println("no such vertex\ncurrent vertex number:"+vertexNum);
            return false;
        }
        if(i==j) {
            System.out.println("can't reduce the weight of an edge between same vertices");
            return false;
        }
        int edgeExist=0;
        prob1Vertex v = verArray.get(i);
        while(v.next!=null) {
            v = v.next;
            if(v.index==j) {
                edgeExist=1;
                break;
            }
        }
        if(edgeExist==0) {
            System.out.println("no edge between "+i+" and "+j);
            return false;
        }
        if(w>=v.weight) {
            System.out.println("please input a lower weight between vertex "+i+" and "+j+"\ncurrent weight: "+v.weight);
            return false;
        }
        v.weight = w;
        return true;
    }
    public ArrayList<prob1Vertex> shortestPath(int i, int j) {
        if(i>vertexNum||j>vertexNum) {
            System.out.println("no such edge");
            return null;
        }
        distance = new int[vertexNum+1];
        preNode = new int[vertexNum+1];
        for(int ii=0;ii<vertexNum+1;ii++) {
            distance[ii] = INF;
            preNode[ii] = NIL;
        }
        distance[i] = 0;
        for(int ii=0;ii<vertexNum-1;ii++) {
            for(int jj=1;jj<vertexNum+1;jj++)
                for(int kk=1;kk<vertexNum+1;kk++){
                    if(kk==jj)
                        continue;
                    relax(jj,kk);
                }
        }


        for(int jj=1;jj<vertexNum+1;jj++)
            for(int kk=1;kk<vertexNum+1;kk++){
                if(kk==jj)
                    continue;
                if(relax(jj,kk)) {
                    System.out.println("negative cycle exists");
                    return null;
                }
            }


        ArrayList<Integer> result = new ArrayList<Integer>();
        System.out.print("shortest path from "+i+" to "+j+":");
        int jj = j;
        while (jj!=i) {
            result.add(jj);
            jj = preNode[jj];
        }
        System.out.print(i);
        for(int ii=result.size()-1;ii>=0;ii--) {
            System.out.print(">>>"+result.get(ii));
        }
        System.out.println("\nthe total distance is "+distance[j]);
        ArrayList<prob1Vertex> vertexList = new ArrayList<prob1Vertex>();
        for(int ii=0;ii<result.size();ii++) {
            vertexList.add(verArray.get(result.get(ii)));
        }
        vertexList.add(verArray.get(i));






        return vertexList;
    }

    private int getWeight(int i, int j) {
        //System.out.println("shold get the weight between "+i+" and "+j);
        prob1Vertex v = verArray.get(i);
        while(v.index!=j) {
            if(v.next==null) {
                return INF;
            }

            v = v.next;
        }
        return v.weight;
    }

    private boolean relax(int u, int v) {
        int w = getWeight(u,v);
        if(w==INF)
            return false;
        if(distance[u]+w<distance[v]) {
            distance[v] = distance[u] + w;
            preNode[v] = u;
            return true;
        }
        return false;
        //System.out.println("distance["+v+"] = "+distance[v]);

    }


}
