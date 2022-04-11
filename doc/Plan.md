```java
private boolean hasAugmentingPath(int s, int t) {
    // TODO:
    for vertex in Graph
        vertiex.parent = -1
    queue.enqueue(s)
    while !queue.isempty() and vertex t.parent == -1:
        v = queue.dequeue()
        for edge in v.to()
            w = edge.to
            residualCapacity = edge.capacity() - edgeUse
            if edge not in queue and edge has residual capacity and is not s:
                w.parent = v
                queue.enqueue(w)
    if vertex t.parent != -1 return true
    return false;
}
```