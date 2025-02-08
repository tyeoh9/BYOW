package core;

import java.util.*;

class Graph<T> {
    class Edge<T> {
        T source;
        T destination;
        int weight;

        public Edge(T source, T destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }
    }
    private final Map<T, List<Edge<T>>> adjList;
    private T lastVertex;

    public Graph() {
        adjList = new TreeMap<>();
    }

    // Add an edge to the graph
    void addEdge(T source, T destination, int weight) {
        if (source.equals(destination)) {
            return;
        }
        lastVertex = source;
        Edge<T> edge = new Edge<>(source, destination, weight);
        adjList.computeIfAbsent(source, k -> new LinkedList<>()).add(edge);
        adjList.computeIfAbsent(destination, k -> new LinkedList<>()).add(new Edge<>(destination, source, weight));
    }

    void addEdge(Edge<T> edge) {
        adjList.computeIfAbsent(edge.source, k -> new LinkedList<>()).add(edge);
        adjList.computeIfAbsent(edge.destination,
                k -> new LinkedList<>()).add(new Edge<>(edge.destination, edge.source, edge.weight));
    }

    // Prim's algorithm to find the MST
    List<Edge<T>> primMST() {
        List<Edge<T>> mst = new ArrayList<>();
        ArrayList<T> visited = new ArrayList<>();
        PriorityQueue<Edge<T>> pq = new PriorityQueue<>(new Comparator<Edge<T>>() {
            @Override
            public int compare(Edge<T> edge1, Edge<T> edge2) {
                return Integer.compare(edge1.weight, edge2.weight);
            }
        });

        // Start from an arbitrary vertex (you can choose any starting vertex)
        T startVertex = lastVertex;
        visited.add(startVertex);
        pq.addAll(adjList.getOrDefault(startVertex, Collections.emptyList()));

        while (!pq.isEmpty()) {
            Edge<T> edge = pq.poll();
            if (visited.contains(edge.destination)) {
                continue;
            }
            mst.add(edge);
            visited.add(edge.destination);
            for (Edge<T> nextEdge : adjList.getOrDefault(edge.destination, Collections.emptyList())) {
                if (!visited.contains(nextEdge.destination)) {
                    pq.add(nextEdge);
                }
            }
        }
        return mst;
    }

    public List<Edge<T>> adj(T node) {
        return adjList.get(node);
    }
}
