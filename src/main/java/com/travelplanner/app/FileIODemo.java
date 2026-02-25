package com.travelplanner.app;

import com.travelplanner.structures.MyGraph;
import java.io.IOException;

/**
 * Demo application to test File I/O functionality
 * This demonstrates loading graph data from map_data.txt
 * 
 * Requirement: Giai đoạn 3 - Đọc dữ liệu bản đồ/đồ thị từ file text (File I/O)
 */
public class FileIODemo {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("📂 FILE I/O DEMO - Loading Graph Data from File");
        System.out.println("=".repeat(60));

        MyGraph graph = new MyGraph();
        String dataFile = "src/main/resource/map_data.txt";

        try {
            // Load data from file
            System.out.println("\n📖 Reading file: " + dataFile);
            graph.loadFromFile(dataFile);

            // Display loaded data
            System.out.println("\n📊 Graph Statistics:");
            System.out.println("   - Total vertices: " + graph.getVertexCount());

            System.out.println("\n📍 Loaded Locations:");
            for (int i = 0; i < graph.getVertexCount(); i++) {
                var loc = graph.getVertex(i);
                System.out.printf("   %d. [%s] %s - %s (%.2f VND)%n",
                        i + 1, loc.getId(), loc.getName(), loc.getDescription(), loc.getPrice());
            }

            System.out.println("\n🛣️  Edges (Routes):");
            for (int i = 0; i < graph.getVertexCount(); i++) {
                for (int j = i + 1; j < graph.getVertexCount(); j++) {
                    int distance = graph.getDistance(i, j);
                    if (distance > 0) {
                        System.out.printf("   %s ↔ %s: %d km%n",
                                graph.getVertex(i).getId(),
                                graph.getVertex(j).getId(),
                                distance);
                    }
                }
            }

            // Test Dijkstra with loaded data
            System.out.println("\n🚀 Testing Dijkstra Algorithm:");
            String startId = "HN";
            String endId = "HCM";
            System.out.println("   Finding shortest path from " + startId + " to " + endId + "...");

            var path = graph.getPath(startId, endId);
            if (path != null && !path.isEmpty()) {
                System.out.print("   Path: ");
                for (int i = 0; i < path.size(); i++) {
                    System.out.print(path.get(i).getName());
                    if (i < path.size() - 1)
                        System.out.print(" → ");
                }
                System.out.println();
            }

            System.out.println("\n✅ File I/O Demo completed successfully!");

        } catch (IOException e) {
            System.err.println("\n❌ Error loading file: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n" + "=".repeat(60));
    }
}
