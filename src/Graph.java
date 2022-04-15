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
        vertices[destination].addEdge(destination, source, 0);

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
                int capacity = residual[v.parent][v.id]; // This is the forward flow capacity
                if (capacity < availableFlow){availableFlow = capacity;}
                v = vertices[v.parent]; // Move the current up a level to its parent
            }
            v = vertices[t];
            while (v.parent != -1){
                residual[v.parent][v.id] -= availableFlow; // Forward flow
                residual[v.id][v.parent] += availableFlow; // Backwards flow
                v = vertices[v.parent];
            }
            totalFlow += availableFlow;
            path.insert(0, "Flow " + availableFlow + ": "+ s + " ");
            totalPath.append(path).append("\n");
        }
        if(report){
            System.out.println("-- Max Flow: " + name+ " --");
            System.out.println(totalPath);
            System.out.print(getEdgeReport());
        }
        return totalFlow;
    }

    /**
     * Algorithm to find the min-cut edges in a network
     */
    public void findMinCut(int s) {
        int[][] finalGraph = getTopological();
        ArrayList<Integer> R = findReachableVertices(s,finalGraph);
//        System.out.println("The reachable vertices of " + s + " are: " + R);
        ArrayList<int[]> cutEdges = findCutEdges(R, finalGraph);
        printCutEdges(cutEdges);
    }

    /**
     * Algorithm to find an augmenting path in a network
     */
    private boolean hasAugmentingPath(int s, int t) {
        Queue<Integer> queue = new LinkedList<>();
        // Reset all vertices to have no parent
        for (var vertex: vertices) {vertex.parent = -1;}
        queue.add(s);
        while (!queue.isEmpty() && vertices[t].parent == -1){
            int v = queue.remove();
            for (var edge: vertices[v].successor){
                int w = edge.to;
                if (residual[v][w] > 0 && vertices[w].parent == -1 && w != s){
                    vertices[w].parent = v;
                    queue.add(w);
                }
            }
        }
        // Return false if the parent is non-existent
        // Return true if the parent has changed
        return vertices[t].parent != -1;
    }

    private String getEdgeReport(){
        // Puts together a string to be output with data from each edge
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

    private int[][] getTopological(){
        // This converts the residual graph into the final usage graph by comparing it with its original and leaving
        // unused edges alone
        int[][] vertices = new int[residual.length][residual.length];
        for (int i = 0; i < residual.length; i++){
            for (int j = 0; j < residual[i].length; j++){
//                vertices[i][j] = forwardMovement[i][j] - residual[i][j];

                // If it didn't have forward movement and is not zero it was a back path
                 if (getCapacity(i, j) == 0 && residual[i][j] != 0 ){vertices[i][j] = -1;}
                else {vertices[i][j] = residual[i][j];}

                // Convert all values to 1, 0, or -1
                if (vertices[i][j] > 0){vertices[i][j] = 1;}
                else if (vertices[i][j] < 0){vertices[i][j] = -1;}
            }
        }
        return vertices;
    }

    private ArrayList<Integer> findReachableVertices(int s, int[][] finalGraph){
        // Using the residual graph, this finds vertices that are not full and within range of the specified start 's'
        ArrayList<Integer> R = new ArrayList<>(residual.length);
        R.add(s);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);
        do {
            int vertex = queue.remove();
            for (int i = 0; i < finalGraph.length; i++){
                if (finalGraph[vertex][i] != 0 && !queue.contains(i) && !R.contains(i)){
                    queue.add(i);
                    if(!R.contains(i)){R.add(i);}
                }
            }
        } while (!queue.isEmpty());
        return R;
    }

    private ArrayList<int[]> findCutEdges(ArrayList<Integer> R, int[][] finalGraph){
        // Using the set of vertices R, this finds the nearest vertices that are not in the set to be cut off
        ArrayList<int[]> cutEdges = new ArrayList<>(residual.length);
        for (int vertex : R){
            for (int i = 0; i < finalGraph.length; i++){
                int[] pair = new int[2];
                if (finalGraph[i][vertex] == -1 && !R.contains(i)){
                    pair[0] = vertex;
                    pair[1] = i;
                    cutEdges.add(pair);
                }
            }
        }
        return cutEdges;
    }

    private int getCapacity(int start, int end){
        GraphNode vertex = vertices[start];
        for (var edge:vertex.successor){
            if (edge.to == end){
                return edge.capacity;
            }
        }
        // This isn't ever actually hit because we know there is an edge going into it
        return 0;
    }

    private void printCutEdges(ArrayList<int[]> cutEdges){
        StringBuilder cutPrint = new StringBuilder();
        cutPrint.append("\n-- Min Cut: " + name + " --\n");
        for (var pair : cutEdges){
            cutPrint.append("Min Cut Edge: (" + pair[0] + ", " + pair[1] + ") : "+ getCapacity(pair[0], pair[1]) +"\n");
        }
        System.out.println(cutPrint);
    }

    // Pass either the residual or another graph in to see what the values are
    private void printGraph(int[][] finalGraph, String name){
        System.out.println("--- "+ name +" ---");
        for (int i = 0; i < finalGraph.length; i++){
            System.out.print(i + ": ");
            for (int j = 0; j < finalGraph[i].length; j++){
                System.out.print(finalGraph[i][j] + " ");
            }
            System.out.println();
        }
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