package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyGraphTest {
    
    // Helper tạo địa điểm nhanh
    private TourLocation createLoc(String id) {
        // Giả sử TourLocation có constructor (id, name, desc, price)
        // Bạn chỉnh lại cho khớp với code TourLocation của bạn nhé
        return new TourLocation(id, "City " + id, "Dep", 100);
    }

    @Test
    public void testAddVertexAndEdge() {
        MyGraph graph = new MyGraph();
        
        // Tạo 3 thành phố A, B, C
        TourLocation locA = createLoc("A");
        TourLocation locB = createLoc("B");
        TourLocation locC = createLoc("C");

        graph.addVertex(locA); // Index 0
        graph.addVertex(locB); // Index 1
        graph.addVertex(locC); // Index 2

        assertEquals(3, graph.getVertexCount());

        // Nối A - B (Khoảng cách 50km)
        graph.addEdge(0, 1, 50);
        
        // Nối B - C (Khoảng cách 100km)
        graph.addEdge(1, 2, 100);

        // Kiểm tra kết nối
        assertEquals(50, graph.getDistance(0, 1)); // A->B
        assertEquals(50, graph.getDistance(1, 0)); // B->A (Vô hướng)
        assertEquals(100, graph.getDistance(1, 2)); // B->C
        
        // A và C chưa nối trực tiếp -> Phải là 0
        assertEquals(0, graph.getDistance(0, 2)); 
    }
    @Test
    public void testDijkstra() {
        MyGraph graph = new MyGraph();

        // A -> B: 10km, B -> C: 10km, A -> C: 50km
        // Đường ngắn nhất A->C phải là A -> B -> C (20km)
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
}