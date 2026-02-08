package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;
import java.util.Stack; // Dùng để in ngược đường đi từ đích về đầu

public class MyGraph {
    private final int MAX_VERTS = 20; // Giới hạn số địa điểm
    private final int INFINITY = 1000000000; // Số cực lớn (tượng trưng cho không có đường)
    private TourLocation[] vertexList; // Danh sách địa điểm
    private int[][] adjMat; // Ma trận kề lưu khoảng cách (km)
    private int nVerts; // Số địa điểm hiện có

    public MyGraph() {
        vertexList = new TourLocation[MAX_VERTS];
        adjMat = new int[MAX_VERTS][MAX_VERTS];
        nVerts = 0;

        // Khởi tạo ma trận: Mặc định khoảng cách là 0 (chưa nối)
        for (int i = 0; i < MAX_VERTS; i++) {
            for (int j = 0; j < MAX_VERTS; j++) {
                adjMat[i][j] = 0;
            }
        }
    }

    public void addVertex(TourLocation loc) {
        if (nVerts < MAX_VERTS) {
            vertexList[nVerts++] = loc;
        } else {
            System.out.println("Đồ thị đã đầy!");
        }
    }

    public void addEdge(int start, int end, int weight) {
        adjMat[start][end] = weight;
        adjMat[end][start] = weight; // Đồ thị vô hướng (2 chiều như nhau)
    }

    // Helper: Tìm index của địa điểm theo ID (Ví dụ: "HN" -> 0)
    public int findIndexById(String id) {
        for (int i = 0; i < nVerts; i++) {
            if (vertexList[i].getId().equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1; // Không tìm thấy
    }

    // ==========================================================
    // 🚀 THUẬT TOÁN DIJKSTRA (TÌM ĐƯỜNG NGẮN NHẤT) 🚀
    // ==========================================================
    public void findShortestPath(String startId, String endId) {
        int startNode = findIndexById(startId);
        int endNode = findIndexById(endId);

        if (startNode == -1 || endNode == -1) {
            System.out.println("Lỗi: Không tìm thấy địa điểm khởi hành hoặc đích đến!");
            return;
        }

        // 1. Khởi tạo các mảng cần thiết
        int[] distance = new int[MAX_VERTS]; // Lưu khoảng cách ngắn nhất từ Start -> i
        int[] parent = new int[MAX_VERTS]; // Lưu vết đường đi (Node cha của i là ai?)
        boolean[] visited = new boolean[MAX_VERTS]; // Đánh dấu đã chốt phương án chưa

        // Cài đặt ban đầu
        for (int i = 0; i < nVerts; i++) {
            distance[i] = INFINITY; // Chưa biết đường thì coi như xa vô tận
            visited[i] = false;
            parent[i] = -1; // Chưa có cha
        }
        distance[startNode] = 0; // Khoảng cách từ mình đến mình là 0

        // 2. Bắt đầu thuật toán
        for (int i = 0; i < nVerts; i++) {
            // Bước A: Chọn đỉnh chưa thăm có khoảng cách nhỏ nhất
            int u = -1;
            int minDist = INFINITY;
            for (int v = 0; v < nVerts; v++) {
                if (!visited[v] && distance[v] < minDist) {
                    minDist = distance[v];
                    u = v;
                }
            }

            // Nếu không còn đỉnh nào để đi hoặc đích đến không thể tới được
            if (u == -1 || distance[u] == INFINITY)
                break;

            visited[u] = true; // Chốt đỉnh u

            // Bước B: "Thư giãn" (Relax) các hàng xóm của u
            for (int v = 0; v < nVerts; v++) {
                // Nếu có đường nối (adjMat[u][v] > 0) và chưa thăm v
                if (adjMat[u][v] != 0 && !visited[v]) {
                    int newDist = distance[u] + adjMat[u][v];
                    // Nếu tìm thấy đường mới ngắn hơn đường cũ
                    if (newDist < distance[v]) {
                        distance[v] = newDist; // Cập nhật khoảng cách
                        parent[v] = u; // Lưu vết: Muốn đến v thì phải qua u
                    }
                }
            }
        }

        // 3. In kết quả đường đi
        printPathResult(startNode, endNode, distance, parent);
    }

    // Helper: In kết quả ra màn hình cho đẹp
    private void printPathResult(int start, int end, int[] distance, int[] parent) {
        if (distance[end] == INFINITY) {
            System.out.println("Rất tiếc! Không có đường đi từ " + vertexList[start].getName()
                    + " đến " + vertexList[end].getName());
            return;
        }

        System.out.println("\n=== KẾT QUẢ TÌM ĐƯỜNG (DIJKSTRA) ===");
        System.out.println("Từ: " + vertexList[start].getName());
        System.out.println("Đến: " + vertexList[end].getName());
        System.out.println("Tổng quãng đường: " + distance[end] + " km");
        System.out.print("Lộ trình: ");

        // Truy vết ngược từ Đích về Đầu (End -> Start) dùng Stack
        Stack<Integer> pathStack = new Stack<>();
        int current = end;
        while (current != -1) {
            pathStack.push(current);
            current = parent[current];
        }

        // In ra từ Stack
        while (!pathStack.isEmpty()) {
            int nodeIdx = pathStack.pop();
            System.out.print(vertexList[nodeIdx].getName());
            if (!pathStack.isEmpty())
                System.out.print(" -> ");
        }
        System.out.println("\n====================================");
    }

    // ==========================================================
    // 🌍 API METHODS CHO WEB SERVER 🌍
    // ==========================================================

    // Lấy danh sách tất cả địa điểm (để hiển thị lên bản đồ và dropdown)
    public java.util.List<TourLocation> getAllLocations() {
        java.util.List<TourLocation> list = new java.util.ArrayList<>();
        for (int i = 0; i < nVerts; i++) {
            list.add(vertexList[i]);
        }
        return list;
    }

    // Tìm đường và trả về danh sách các địa điểm (thay vì in ra console)
    public java.util.List<TourLocation> getPath(String startId, String endId) {
        int startNode = findIndexById(startId);
        int endNode = findIndexById(endId);

        if (startNode == -1 || endNode == -1)
            return null;

        // --- Tái sử dụng logic Dijkstra (Copy từ trên xuống hoặc tách hàm riêng) ---
        // Để nhanh gọn, mình viết lại phần core Dijkstra ở đây
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

            if (u == -1 || distance[u] == INFINITY)
                break;
            visited[u] = true;

            for (int v = 0; v < nVerts; v++) {
                if (adjMat[u][v] != 0 && !visited[v]) {
                    int newDist = distance[u] + adjMat[u][v];
                    if (newDist < distance[v]) {
                        distance[v] = newDist;
                        parent[v] = u;
                    }
                }
            }
        }
        // ------------------------------------------------------------

        if (distance[endNode] == INFINITY)
            return null; // Không có đường

        // Truy vết để tạo List kết quả
        java.util.List<TourLocation> path = new java.util.ArrayList<>();
        int current = endNode;
        while (current != -1) {
            path.add(vertexList[current]);
            current = parent[current];
        }
        java.util.Collections.reverse(path); // Đảo ngược để có Start -> End
        return path;
    }

    // Helper class cho Edge (bên trong MyGraph hoặc tách riêng, mình để trong cho
    // gọn)
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

    // Lấy danh sách các cạnh để vẽ bản đồ
    public java.util.List<Edge> getAllEdges() {
        java.util.List<Edge> edges = new java.util.ArrayList<>();
        for (int i = 0; i < nVerts; i++) {
            for (int j = i + 1; j < nVerts; j++) { // Duyệt tam giác trên để không lặp lại (vô hướng)
                if (adjMat[i][j] > 0) {
                    edges.add(new Edge(vertexList[i].getId(), vertexList[j].getId(), adjMat[i][j]));
                }
            }
        }
        return edges;
    }

    // Các hàm Getter hỗ trợ Unit Test
    public int getVertexCount() {
        return nVerts;
    }

    public int getDistance(int start, int end) {
        return adjMat[start][end];
    }

    public TourLocation getVertex(int index) {
        return vertexList[index];
    }
}