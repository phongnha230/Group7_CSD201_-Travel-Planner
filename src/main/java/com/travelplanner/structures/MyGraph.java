package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class MyGraph {
    private static final int MAX_VERTS = 20;
    private static final int INFINITY = 1000000000;

    private final TourLocation[] vertexList;
    private final java.util.List<java.util.List<Neighbor>> adjList;
    private int nVerts;

    private static class Neighbor {
        int to;
        int weight;

        Neighbor(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    public MyGraph() {
        vertexList = new TourLocation[MAX_VERTS];
        adjList = new java.util.ArrayList<>();
        nVerts = 0;

        for (int i = 0; i < MAX_VERTS; i++) {
            adjList.add(new java.util.ArrayList<>());
        }
    }

    public void addVertex(TourLocation loc) {
        if (nVerts < MAX_VERTS) {
            vertexList[nVerts++] = loc;
        } else {
            System.out.println("Graph is full!");
        }
    }

    public void addEdge(int start, int end, int weight) {
        upsertNeighbor(start, end, weight);
        upsertNeighbor(end, start, weight);
    }

    public void findShortestPath(String startId, String endId) {
        int startNode = findIndexById(startId);
        int endNode = findIndexById(endId);

        if (startNode == -1 || endNode == -1) {
            System.out.println("Error: Start or end location not found!");
            return;
        }

        int[] distance = new int[MAX_VERTS];
        int[] parent = new int[MAX_VERTS];
        boolean[] visited = new boolean[MAX_VERTS];

        for (int i = 0; i < nVerts; i++) {
            distance[i] = INFINITY;
            visited[i] = false;
            parent[i] = -1;
        }
        distance[startNode] = 0;

        for (int i = 0; i < nVerts; i++) {
            int u = -1;
            int minDist = INFINITY;
            for (int v = 0; v < nVerts; v++) {
                if (!visited[v] && distance[v] < minDist) {
                    minDist = distance[v];
                    u = v;
                }
            }

            if (u == -1 || distance[u] == INFINITY) {
                break;
            }

            visited[u] = true;

            for (Neighbor neighbor : adjList.get(u)) {
                int v = neighbor.to;
                if (!visited[v]) {
                    int newDist = distance[u] + neighbor.weight;
                    if (newDist < distance[v]) {
                        distance[v] = newDist;
                        parent[v] = u;
                    }
                }
            }
        }

        printPathResult(startNode, endNode, distance, parent);
    }

    private void printPathResult(int start, int end, int[] distance, int[] parent) {
        if (distance[end] == INFINITY) {
            System.out.println("No path from " + vertexList[start].getName() + " to " + vertexList[end].getName());
            return;
        }

        System.out.println("\n=== SHORTEST PATH RESULT (DIJKSTRA) ===");
        System.out.println("From: " + vertexList[start].getName());
        System.out.println("To: " + vertexList[end].getName());
        System.out.println("Total distance: " + distance[end] + " km");
        System.out.print("Path: ");

        Stack<Integer> pathStack = new Stack<>();
        int current = end;
        while (current != -1) {
            pathStack.push(current);
            current = parent[current];
        }

        while (!pathStack.isEmpty()) {
            int nodeIdx = pathStack.pop();
            System.out.print(vertexList[nodeIdx].getName());
            if (!pathStack.isEmpty()) {
                System.out.print(" -> ");
            }
        }
        System.out.println("\n====================================");
    }

    public java.util.List<TourLocation> getAllLocations() {
        java.util.List<TourLocation> list = new java.util.ArrayList<>();
        for (int i = 0; i < nVerts; i++) {
            list.add(vertexList[i]);
        }
        return list;
    }

    /**
     * Load graph data from text file
     * Format:
     * VERTEX|id|name|description|price|x|y
     * EDGE|startId|endId|weight
     *
     * @param filename Path to the data file
     * @throws IOException if file cannot be read
     */
    public void loadFromFile(String filename) throws IOException {
        String line;
        int lineNumber = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\|");

                if (parts[0].equals("VERTEX")) {
                    if (parts.length != 7) {
                        System.err.println("Warning: Invalid VERTEX format at line " + lineNumber);
                        continue;
                    }

                    String id = parts[1].trim();
                    String name = parts[2].trim();
                    String description = parts[3].trim();
                    double price = Double.parseDouble(parts[4].trim());
                    int x = Integer.parseInt(parts[5].trim());
                    int y = Integer.parseInt(parts[6].trim());

                    TourLocation location = new TourLocation(id, name, description, price, x, y);
                    addVertex(location);

                } else if (parts[0].equals("EDGE")) {
                    if (parts.length != 4) {
                        System.err.println("Warning: Invalid EDGE format at line " + lineNumber);
                        continue;
                    }

                    String startId = parts[1].trim();
                    String endId = parts[2].trim();
                    int weight = Integer.parseInt(parts[3].trim());

                    int startIdx = findIndexById(startId);
                    int endIdx = findIndexById(endId);

                    if (startIdx == -1 || endIdx == -1) {
                        System.err.println("Warning: Vertex not found for edge at line " + lineNumber);
                        continue;
                    }

                    addEdge(startIdx, endIdx, weight);
                }
            }

            System.out.println("Loaded " + nVerts + " vertices from file: " + filename);

        } catch (NumberFormatException e) {
            System.err.println("Error parsing number at line " + lineNumber + ": " + e.getMessage());
            throw new IOException("Invalid number format in file", e);
        }
    }

    public java.util.List<TourLocation> getPath(String startId, String endId) {
        int startNode = findIndexById(startId);
        int endNode = findIndexById(endId);

        if (startNode == -1 || endNode == -1) {
            return null;
        }

        int[] distance = new int[MAX_VERTS];
        int[] parent = new int[MAX_VERTS];
        boolean[] visited = new boolean[MAX_VERTS];

        for (int i = 0; i < nVerts; i++) {
            distance[i] = INFINITY;
            visited[i] = false;
            parent[i] = -1;
        }
        distance[startNode] = 0;

        for (int i = 0; i < nVerts; i++) {
            int u = -1;
            int minDist = INFINITY;
            for (int v = 0; v < nVerts; v++) {
                if (!visited[v] && distance[v] < minDist) {
                    minDist = distance[v];
                    u = v;
                }
            }

            if (u == -1 || distance[u] == INFINITY) {
                break;
            }
            visited[u] = true;

            for (Neighbor neighbor : adjList.get(u)) {
                int v = neighbor.to;
                if (!visited[v]) {
                    int newDist = distance[u] + neighbor.weight;
                    if (newDist < distance[v]) {
                        distance[v] = newDist;
                        parent[v] = u;
                    }
                }
            }
        }

        if (distance[endNode] == INFINITY) {
            return null;
        }

        java.util.List<TourLocation> path = new java.util.ArrayList<>();
        int current = endNode;
        while (current != -1) {
            path.add(vertexList[current]);
            current = parent[current];
        }
        java.util.Collections.reverse(path);
        return path;
    }

    public static class Edge {
        public String startId;
        public String endId;
        public int weight;

        public Edge(String startId, String endId, int weight) {
            this.startId = startId;
            this.endId = endId;
            this.weight = weight;
        }
    }

    public java.util.List<Edge> getAllEdges() {
        java.util.List<Edge> edges = new java.util.ArrayList<>();
        for (int i = 0; i < nVerts; i++) {
            for (Neighbor neighbor : adjList.get(i)) {
                int j = neighbor.to;
                if (i < j) {
                    edges.add(new Edge(vertexList[i].getId(), vertexList[j].getId(), neighbor.weight));
                }
            }
        }
        return edges;
    }

    public int getVertexCount() {
        return nVerts;
    }

    public int getDistance(int start, int end) {
        if (start < 0 || start >= nVerts || end < 0 || end >= nVerts) {
            return 0;
        }
        for (Neighbor neighbor : adjList.get(start)) {
            if (neighbor.to == end) {
                return neighbor.weight;
            }
        }
        return 0;
    }

    public TourLocation getVertex(int index) {
        return vertexList[index];
    }

    private int findIndexById(String id) {
        for (int i = 0; i < nVerts; i++) {
            if (vertexList[i].getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void upsertNeighbor(int from, int to, int weight) {
        java.util.List<Neighbor> neighbors = adjList.get(from);
        for (Neighbor neighbor : neighbors) {
            if (neighbor.to == to) {
                neighbor.weight = weight;
                return;
            }
        }
        neighbors.add(new Neighbor(to, weight));
    }
}
