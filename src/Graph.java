import java.util.*;

public class Graph {
    private final GraphNode[] vertices;  // Adjacency list for graph.
    private final String name;  //The file from which the graph was created.
    private int[][] residual;

    public Graph(String name, int vertexCount) {
        this.name = name;

        vertices = new GraphNode[vertexCount];
        residual = new int[vertexCount][vertexCount]; // in case one vertex touches all others
        for (int vertex = 0; vertex < vertexCount; vertex++) {
            vertices[vertex] = new GraphNode(vertex);
        }
    }

    public boolean addEdge(int source, int destination, int capacity) {
        // A little bit of validation
        if (source < 0 || source >= vertices.length) return false;
        if (destination < 0 || destination >= vertices.length) return false;

        // This adds the actual requested edge, along with its capacity
        vertices[source].addEdge(source, destination, capacity);
        residual[source][destination] = capacity;
        residual[destination][source]= 0;
        return true;
    }

    /**
     * Algorithm to find max-flow in a network
     */
    public int findMaxFlow(int s, int t, boolean report) {
        int totalFlow = 0;
        StringBuilder totalPath = new StringBuilder();
        while (hasAugmentingPath(s, t)){
            StringBuilder path = new StringBuilder();
            int availableFlow = 1000000;
            GraphNode v = vertices[t];
            while (v.parent != -1){
                path.insert(0, v.id + " ");
                int capacity = getCapacity(v.id);
//                System.out.println("Capacity from " + v.parent + " to " + v.id + " is "+ capacity);
                if (capacity < availableFlow){availableFlow = capacity;}
                if (residual[v.parent][v.id] < availableFlow){availableFlow = residual[v.parent][v.id];}
                v = vertices[v.parent]; // Move the current up a level to its parent
            }
            v = vertices[t];
            while (v.parent != -1){
                residual[v.parent][v.id] -= availableFlow;
                residual[v.id][v.parent] += availableFlow;
                v = vertices[v.parent];
            }
            path.insert(0, "Flow " + availableFlow + ": "+ s + " ");
            totalPath.append(path).append("\n");
            totalFlow += availableFlow;
        }
        if(report){
            System.out.println("-- Max Flow: " + name+ " --");
            System.out.println(totalPath);
            System.out.println(getEdgeReport());
        }
        return totalFlow;
    }

    private String getEdgeReport(){
        StringBuilder sb = new StringBuilder();
        for (var vertex : vertices){
            for (var edge: vertex.successor){
                int diff = edge.capacity - residual[edge.from][edge.to];
                if (diff > 0){
                    sb.append("Edge(" + edge.from + ", " + edge.to + ") transports " + diff + " items");
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Algorithm to find the capacity of an edge from a parent to a given node
     * @param current node to
     * @return integer value of the capacity to a node
     */
    private int getCapacity(int current){
        GraphNode vertex = vertices[current];
        GraphNode parent = vertices[vertex.parent];
        for (GraphNode.EdgeInfo edge: parent.successor){
            if (edge.to == current && edge.from == parent.id){
                return edge.capacity;
            }
        }
        return -1;
    }

    /**
     * Algorithm to find an augmenting path in a network
     */
    private boolean hasAugmentingPath(int s, int t) {
        Queue<Integer> queue = new LinkedList<>();
        // Reset all vertices to have no parent
//        System.out.println("Augmenting paths values are s:" + s + " t:" + t);
        for (var vertex: vertices) {
            vertex.parent = -1;
        }
        queue.add(s);
        while (!queue.isEmpty() && vertices[t].parent == -1){
            int v = queue.remove();
            for (var edge: vertices[v].successor){
                int w = edge.to;
                if (vertices[w].parent == -1 && residual[v][w] > 0 && w != s){
                    vertices[w].parent = v;
                    queue.add(w);
                }
            }
        }
        // Return false if the parent is non existent
        // Return true if the parent has changed
        return vertices[t].parent != -1;
    }


    /**
     * Algorithm to find the min-cut edges in a network
     */
    public void findMinCut(int s) {
        // TODO:
    }

    public void printResidual() {

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("The Graph " + name + " \n");
        for (var vertex : vertices) {
            sb.append((vertex.toString()));
        }
        return sb.toString();
    }
}
