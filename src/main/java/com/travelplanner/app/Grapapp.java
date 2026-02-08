
package com.travelplanner.app;

import com.travelplanner.entities.TourLocation;
import com.travelplanner.structures.MyGraph;
public class Grapapp {
    
    public static void main(String[] args) {
        System.out.println("=== TEST THUẬT TOÁN DIJKSTRA (TÌM ĐƯỜNG) ===");
        
        // 1. Khởi tạo đồ thị
        MyGraph graph = new MyGraph();

        // 2. Tạo các địa điểm (Node)
        // Giả sử constructor là: id, name, description, price
        graph.addVertex(new TourLocation("HN", "Ha Noi", "Thu do", 0));      // Index 0
        graph.addVertex(new TourLocation("DN", "Da Nang", "Bien dep", 0));   // Index 1
        graph.addVertex(new TourLocation("HCM", "Ho Chi Minh", "Sai Gon", 0)); // Index 2
        graph.addVertex(new TourLocation("NT", "Nha Trang", "Bien xanh", 0)); // Index 3
        graph.addVertex(new TourLocation("DL", "Da Lat", "Mong mo", 0));     // Index 4

        // 3. Tạo đường đi (Edge) và khoảng cách (Weight)
        // HN -> ĐN (700km)
        graph.addEdge(0, 1, 700); 
        // ĐN -> HCM (900km)
        graph.addEdge(1, 2, 900);
        // ĐN -> Nha Trang (500km)
        graph.addEdge(1, 3, 500);
        // Nha Trang -> Đà Lạt (150km)
        graph.addEdge(3, 4, 150);
        // Đà Lạt -> HCM (300km)
        graph.addEdge(4, 2, 300);
        
        // Thử thách: Tìm đường từ Đà Nẵng (DN) đi HCM (HCM)
        // Đường 1: DN -> HCM (Thẳng): 900km
        // Đường 2: DN -> Nha Trang -> Đà Lạt -> HCM: 500 + 150 + 300 = 950km (Xa hơn)
        // Nhưng nếu ta sửa lại đường ĐN -> Nha Trang còn 200km thì sao?
        
        System.out.println("\n--- Kịch bản 1: Đi từ Đà Nẵng -> HCM ---");
        graph.findShortestPath("DN", "HCM");

        System.out.println("\n--- Kịch bản 2: Đi từ Hà Nội -> Đà Lạt ---");
        // HN -> DN -> Nha Trang -> DL (700 + 500 + 150 = 1350km)
        graph.findShortestPath("HN", "DL");
    }
}