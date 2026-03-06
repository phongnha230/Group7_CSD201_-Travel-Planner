package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyGraphTest {

    private TourLocation createLoc(String id) {
        return new TourLocation(id, "City " + id, "Dep", 100);
    }

    private TourLocation createLoc(String id, double price) {
        return new TourLocation(id, "City " + id, "Dep", price);
    }

    @Test
    public void testAddVertexAndEdge() {
        MyGraph graph = new MyGraph();

        TourLocation locA = createLoc("A");
        TourLocation locB = createLoc("B");
        TourLocation locC = createLoc("C");

        graph.addVertex(locA);
        graph.addVertex(locB);
        graph.addVertex(locC);

        assertEquals(3, graph.getVertexCount());

        graph.addEdge(0, 1, 50);
        graph.addEdge(1, 2, 100);

        assertEquals(50, graph.getDistance(0, 1));
        assertEquals(50, graph.getDistance(1, 0));
        assertEquals(100, graph.getDistance(1, 2));
        assertEquals(0, graph.getDistance(0, 2));
    }

    @Test
    public void testDijkstra() {
        MyGraph graph = new MyGraph();

        graph.addVertex(createLoc("A"));
        graph.addVertex(createLoc("B"));
        graph.addVertex(createLoc("C"));

        graph.addEdge(0, 1, 10);
        graph.addEdge(1, 2, 10);
        graph.addEdge(0, 2, 50);

        java.util.List<TourLocation> path = graph.getPath("A", "C");
        assertNotNull(path);
        assertEquals(3, path.size());
        assertEquals("A", path.get(0).getId());
        assertEquals("B", path.get(1).getId());
        assertEquals("C", path.get(2).getId());
    }

    @Test
    public void testLowestCostCanDifferFromShortestDistance() {
        MyGraph graph = new MyGraph();

        graph.addVertex(createLoc("A", 100));
        graph.addVertex(createLoc("B", 100));
        graph.addVertex(createLoc("C", 500));
        graph.addVertex(createLoc("D", 20));

        graph.addEdge(0, 2, 3);
        graph.addEdge(2, 3, 3);
        graph.addEdge(0, 1, 4);
        graph.addEdge(1, 3, 4);

        java.util.List<TourLocation> shortestPath = graph.getPath("A", "D", "distance");
        java.util.List<TourLocation> lowestCostPath = graph.getPath("A", "D", "cost");

        assertNotNull(shortestPath);
        assertNotNull(lowestCostPath);
        assertEquals("C", shortestPath.get(1).getId());
        assertEquals("B", lowestCostPath.get(1).getId());
    }

    @Test
    public void testMapDataLowestCostDiffersForHanoiToHcm() throws Exception {
        MyGraph graph = new MyGraph();
        graph.loadFromFile("src/main/resource/map_data.txt");

        java.util.List<TourLocation> shortestPath = graph.getPath("HN", "HCM", "distance");
        java.util.List<TourLocation> lowestCostPath = graph.getPath("HN", "HCM", "cost");

        assertNotNull(shortestPath);
        assertNotNull(lowestCostPath);
        assertEquals("DN", shortestPath.get(1).getId());
        assertEquals("HCM", shortestPath.get(shortestPath.size() - 1).getId());
        assertEquals("NT", lowestCostPath.get(2).getId());
        assertEquals("DL", lowestCostPath.get(3).getId());
    }
}
