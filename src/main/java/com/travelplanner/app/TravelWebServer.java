package com.travelplanner.app;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.travelplanner.entities.TourLocation;
import com.travelplanner.entities.Customer;
import com.travelplanner.structures.MyGraph;
import com.travelplanner.structures.MyLinkedList;
import com.travelplanner.structures.MyBST;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.net.InetSocketAddress;
import java.util.List;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class TravelWebServer {
    private static MyGraph graph;
    private static MyLinkedList tourList;
    private static MyBST customerTree;

    public static void main(String[] args) throws IOException {
        // 1. Khởi tạo dữ liệu (Graph, Tour List, Customer Tree)
        initData();

        // 2. Tạo HTTP Server tại port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // 3. Định nghĩa các API endpoints
        // Graph APIs
        server.createContext("/api/locations", new LocationsHandler());
        server.createContext("/api/find-path", new FindPathHandler());

        // Tour (Linked List) APIs
        server.createContext("/api/tour", new TourHandler(tourList, graph));

        // Image Upload API
        server.createContext("/api/upload", new UploadHandler());

        // Customer (BST) APIs
        server.createContext("/api/customers", new CustomerHandler(customerTree));

        // 4. Định nghĩa Static File Handler (phục vụ HTML/CSS/JS)
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default executor
        System.out.println("Starting server on port 8080...");
        System.out.println("Open http://localhost:8080 in your browser.");
        server.start();
    }

    private static void initData() {
        // Initialize Graph
        initGraphData();

        // Initialize Tour List (Linked List)
        tourList = new MyLinkedList();
        // Add some sample tour locations
        tourList.addLocation(new TourLocation("HN", "Ha Noi", "Thu do", 0, 300, 100));
        tourList.addLocation(new TourLocation("DN", "Da Nang", "Bien dep", 0, 400, 300));
        tourList.addLocation(new TourLocation("HCM", "Ho Chi Minh", "Sai Gon", 0, 350, 500));

        // Initialize Customer Tree (BST)
        customerTree = new MyBST();
        // Add some sample customers
        customerTree.insert(new Customer("CUS001", "Nguyen Van A", "0901234567", "a@gmail.com"));
        customerTree.insert(new Customer("CUS002", "Tran Thi B", "0912345678", "b@gmail.com"));
        customerTree.insert(new Customer("CUS003", "Le Van C", "0923456789", "c@gmail.com"));
    }

    private static void initGraphData() {
        graph = new MyGraph();

        // Try to load from file first
        String dataFile = "src/main/resource/map_data.txt";
        try {
            graph.loadFromFile(dataFile);
            System.out.println("✅ Graph data loaded from file successfully!");
        } catch (IOException e) {
            System.out.println("⚠️ Could not load from file: " + e.getMessage());
            System.out.println("📝 Using hard-coded data as fallback...");

            // Fallback: Hard-coded data
            graph.addVertex(new TourLocation("HN", "Ha Noi", "Thu do", 50.0, 300, 100));
            graph.addVertex(new TourLocation("DN", "Da Nang", "Bien dep", 80.0, 400, 300));
            graph.addVertex(new TourLocation("HCM", "Ho Chi Minh", "Sai Gon", 100.0, 350, 500));
            graph.addVertex(new TourLocation("NT", "Nha Trang", "Bien xanh", 75.0, 450, 400));
            graph.addVertex(new TourLocation("DL", "Da Lat", "Mong mo", 60.0, 300, 400));

            graph.addEdge(0, 1, 700); // HN - DN
            graph.addEdge(1, 2, 900); // DN - HCM
            graph.addEdge(1, 3, 500); // DN - NT
            graph.addEdge(3, 4, 150); // NT - DL
            graph.addEdge(4, 2, 300); // DL - HCM
        }
    }

    // Handler cho /api/locations
    static class LocationsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            try {
                // CORS headers
                t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

                // Lấy danh sách locations và edges
                List<TourLocation> locations = graph.getAllLocations();
                List<MyGraph.Edge> edges = graph.getAllEdges();

                // Construct JSON: { "locations": [...], "edges": [...] }
                StringBuilder json = new StringBuilder("{ \"locations\": [");

                for (int i = 0; i < locations.size(); i++) {
                    TourLocation loc = locations.get(i);
                    json.append(String.format("{\"id\":\"%s\", \"name\":\"%s\", \"x\":%d, \"y\":%d}",
                            loc.getId(), loc.getName(), loc.getX(), loc.getY()));
                    if (i < locations.size() - 1)
                        json.append(",");
                }
                json.append("], \"edges\": [");

                for (int i = 0; i < edges.size(); i++) {
                    MyGraph.Edge edge = edges.get(i);
                    json.append(String.format("{\"start\":\"%s\", \"end\":\"%s\", \"weight\":%d}",
                            edge.startId, edge.endId, edge.weight));
                    if (i < edges.size() - 1)
                        json.append(",");
                }
                json.append("] }");

                String response = json.toString();
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (IOException | RuntimeException e) {
                System.err.println("LocationsHandler error: " + e.getMessage());
                String error = "{\"error\": \"" + e.getMessage() + "\"}";
                byte[] errorBytes = error.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(500, errorBytes.length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(errorBytes);
                }
            }
        }
    }

    // Handler cho /api/find-path
    static class FindPathHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // CORS
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            // Parse Query Params
            URI requestedUri = t.getRequestURI();
            String query = requestedUri.getQuery();
            Map<String, String> params = queryToMap(query);

            String startId = params.get("start");
            String endId = params.get("end");

            String response;
            int statusCode = 200;

            if (startId == null || endId == null) {
                response = "{\"error\": \"Missing start or end parameter\"}";
                statusCode = 400;
            } else {
                List<TourLocation> path = graph.getPath(startId, endId);

                if (path == null) {
                    response = "{\"error\": \"No path found\"}";
                } else {
                    // Convert path to JSON
                    StringBuilder json = new StringBuilder("[");
                    for (int i = 0; i < path.size(); i++) {
                        TourLocation loc = path.get(i);
                        json.append(String.format("{\"id\":\"%s\", \"name\":\"%s\", \"x\":%d, \"y\":%d}",
                                loc.getId(), loc.getName(), loc.getX(), loc.getY()));
                        if (i < path.size() - 1)
                            json.append(",");
                    }
                    json.append("]");
                    response = json.toString();
                }
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(statusCode, bytes.length);
            try (OutputStream os = t.getResponseBody()) {
                os.write(bytes);
            }
        }

        private Map<String, String> queryToMap(String query) {
            Map<String, String> result = new HashMap<>();
            if (query == null)
                return result;
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    result.put(pair[0], pair[1]);
                } else {
                    result.put(pair[0], "");
                }
            }
            return result;
        }
    }

    // Handler phục vụ file tĩnh (HTML, CSS, JS)
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String path = t.getRequestURI().getPath();
            if ("/".equals(path))
                path = "/index.html"; // Mặc định về index.html

            // Đường dẫn tới folder resource/public
            // Lưu ý: Cần chỉnh lại đường dẫn tuyệt đối cho phù hợp với máy của bạn hoặc
            // dùng class loader
            // Cách đơn giản nhất: Dùng đường dẫn tương đối từ project root
            File file = new File("src/main/resource/public" + path);

            if (file.exists()) {
                t.sendResponseHeaders(200, file.length());
                try (OutputStream os = t.getResponseBody()) {
                    Files.copy(file.toPath(), os);
                }
            } else {
                String response = "404 Main Not Found (File: " + file.getAbsolutePath() + ")";
                byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(404, bytes.length);
                try (OutputStream os = t.getResponseBody()) {
                    os.write(bytes);
                }
            }
        }
    }
}
