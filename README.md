# Travel Planner Pro (Group 7)

Ứng dụng lập kế hoạch du lịch dựa trên Java, minh họa các cấu trúc dữ liệu cơ bản: **Linked List**, **Binary Search Tree (BST)**, và **Graph**.

**Pair Programming:** Nhã and Linh

## Tính năng (DSA)

| Module | Cấu trúc dữ liệu | Chức năng |
|--------|------------------|-----------|
| **Itinerary** | Linked List | Thêm/xóa địa điểm vào tour, duyệt từ Head → Tail |
| **Customers** | BST | Thêm/xóa/tìm kiếm khách hàng theo ID |
| **Map Optimizer** | Graph | Tìm đường ngắn nhất (Dijkstra) theo khoảng cách hoặc thời gian |

## Yêu cầu hệ thống

- **JDK 21** trở lên
- **Maven** (quản lý dependency và build)

## Cách chạy

### 1. Build project

```bash
mvn compile
```

### 2. Chạy ứng dụng web

```bash
mvn exec:java -Dexec.mainClass="com.travelplanner.app.TravelWebServer"
```

### 3. Mở trình duyệt

Truy cập: **http://localhost:8080**

## Hướng dẫn Demo

### Itinerary (Linked List)
1. Chọn địa điểm từ dropdown → chọn vị trí thêm (Head/Tail/Index) → **Add to Itinerary**
2. Xem danh sách tour hiển thị dạng linked list (Head → … → Tail)
3. Bấm **Remove** trên từng thẻ để xóa địa điểm
4. (Tuỳ chọn) Upload ảnh cho địa điểm → bấm vào thẻ có ảnh để set làm nền

### Customers (BST)
1. **Add Customer** → nhập ID, tên, SĐT, email → thêm khách hàng
2. Xem cây BST và bảng danh sách khách hàng
3. **Tìm kiếm:** nhập ID (vd: `001` hoặc `CUS001`) → Enter
4. **Xóa:** bấm icon 🗑️ bên cạnh khách hàng

### Map Optimizer (Graph)
1. Chọn **Starting City** và **Destination City**
2. Chọn tiêu chí: **Distance** hoặc **Time**
3. Bấm **Find Shortest Path** → xem đường đi tối ưu trên bản đồ

## Cấu trúc project

```
src/main/java/com/travelplanner/
├── app/
│   ├── TravelWebServer.java    # Entry point - HTTP server
│   ├── Handlers.java           # API handlers (Tour, Customer, Graph)
│   └── UploadHandler.java      # Upload ảnh
├── entities/
│   ├── TourLocation.java
│   └── Customer.java
└── structures/
    ├── MyLinkedList.java       # Singly Linked List (Tour)
    ├── MyBST.java              # Binary Search Tree (Customers)
    └── MyGraph.java            # Graph + Dijkstra (Map)
```

## Chi tiết kỹ thuật (DSA)

| Cấu trúc | Độ phức tạp | Mô tả |
|----------|-------------|-------|
| **MyLinkedList** | Insert O(1) tail, Delete O(n) | Singly linked list với tail pointer, thêm cuối O(1) |
| **MyBST** | Search O(log n), Insert/Delete O(log n) | Binary Search Tree, tìm kiếm khách hàng theo ID |
| **MyGraph** | Dijkstra O(V²) | Đồ thị vô hướng, tìm đường ngắn nhất qua thuật toán Dijkstra |

## Công nghệ sử dụng

- **Backend:** Java 21, `com.sun.net.httpserver` (HTTP server built-in)
- **Frontend:** HTML5, CSS3, JavaScript (vanilla)
- **Build:** Maven 3.x

## API Endpoints

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/tour` | Lấy danh sách tour (Linked List) |
| POST | `/api/tour?id=&position=&price=` | Thêm địa điểm vào tour |
| DELETE | `/api/tour?id=` | Xóa địa điểm khỏi tour |
| GET | `/api/customers` | Lấy danh sách khách hàng (BST) |
| GET | `/api/customers?id=` | Tìm khách hàng theo ID |
| POST | `/api/customers?id=&name=&phone=&email=` | Thêm khách hàng |
| DELETE | `/api/customers?id=` | Xóa khách hàng |
| GET | `/api/locations` | Lấy danh sách địa điểm và cạnh (Graph) |
| POST | `/api/find-path` | Tìm đường ngắn nhất (Dijkstra) |
| POST | `/api/upload` | Upload ảnh địa điểm |

## Chạy test

```bash
mvn test
```

## Gặp lỗi khi chạy?

- **Port 8080 đã được sử dụng:** Tắt ứng dụng đang chạy trên port 8080 hoặc đổi port trong `TravelWebServer.java`
- **Class not found:** Đảm bảo đã chạy `mvn compile` và dùng đúng main class: `com.travelplanner.app.TravelWebServer`

## Môn học

Dự án thực hiện cho môn **CSD201 - Cấu trúc dữ liệu và giải thuật**.

## GitHub

[https://github.com/phongnha230/Group7_CSD201_-Travel-Planner](https://github.com/phongnha230/Group7_CSD201_-Travel-Planner)
