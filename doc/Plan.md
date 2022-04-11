```java
public int findMaxFlow(int s, int t, boolean report) {
    int totalFlow = 0
    while hasAugmentingPath(s, t){
        availableFlow=inf
        path=""
        vertex v = t
        while v.parent!=-1{
            path+=v.id
            if capacity < availableFlow:
                avaialabelFlow = capacity
            if residual[v.parent][v] < availableFlow:
                availableFlow = residual
            v=v.parent
        }
        v = t
        while v.parent!=-1{
            residual[v.parent][v] -= available flow
            residual[v][v.parent] += available flow
        }
        totalFlow += availableFlow
    }
    if(report){
        System.out.println(Path);
    }
    return totalFlow;
}

```


```java
private boolean hasAugmentingPath(int s, int t) {
    for vertex in Graph
        vertiex.parent = -1
    queue.enqueue(s)
    while !queue.isempty() and vertex t.parent == -1:
        v = queue.dequeue()
        for edge in v.to()
            w = edge.to
            residualCapacity = edge.capacity - residual[v][w]
            if parent != 1 and residualCap > 0 and w is not s:
                w.parent = v
                queue.enqueue(w)
    if vertex t.parent != -1 return true
    return false;
}
```
How to store the edgeUse in order to get a residualCapacity?
    - Create a 2d array residual[to][from] = residualCapacity
