Hãy tạo cho tôi một project Java Maven tên là TravelPlanner_GroupXX với cấu trúc thư mục và file như sau:

Yêu cầu chung:

Tuân theo chuẩn Maven (src/main/java, src/main/resources, src/test/java)

Áp dụng tư duy OOP và Cấu trúc dữ liệu & Giải thuật

Không sử dụng các cấu trúc dữ liệu có sẵn của Java cho phần core logic

Backend Java

Trong src/main/java, tạo package:
com.travelplanner

1. app

ConsoleApp.java: class chứa main() để chạy chương trình console, dùng để demo các cấu trúc dữ liệu trong giai đoạn đầu.

WebServer.java: class dùng để khởi động web server khi mở rộng sang web.

2. entities

TourLocation.java: đại diện cho một điểm du lịch (id, name, description, cost,…).

Customer.java: đại diện cho khách hàng (id, name, email,…).

3. structures (quan trọng nhất)

Node.java: lớp Node cơ bản dùng chung.

MyLinkedList.java: tự cài đặt Linked List để quản lý danh sách Tour.

MyBST.java: tự cài đặt Binary Search Tree để quản lý Customer.

MyGraph.java: tự cài đặt Graph và thuật toán Dijkstra để tìm đường đi ngắn nhất.

4. utils

FileLoader.java: đọc dữ liệu từ file text.

InputValidator.java: kiểm tra dữ liệu đầu vào.

Resources

Trong src/main/resources:

data/map_data.txt: dữ liệu bản đồ.

data/customers.txt: dữ liệu khách hàng.

public/: chứa frontend

index.html

style.css

script.js

assets/

Unit Test

Trong src/test/java/com/travelplanner/structures:

MyLinkedListTest.java

MyBSTTest.java

Mỗi file Java chỉ cần tạo skeleton code (class + package đúng), chưa cần cài đặt chi tiết logic.


Output: 
TravelPlanner_GroupXX/
├── src/
│   ├── main/
│   │   ├── java/                   <-- Nơi chứa code Java (Backend Logic)
│   │   │   └── com/
│   │   │       └── travelplanner/
│   │   │           ├── app/        <-- Chứa hàm Main chạy chương trình
│   │   │           │   ├── ConsoleApp.java    (Chạy demo Console tuần 1-9)
│   │   │           │   └── WebServer.java     (Chạy Web Server tuần 10)
│   │   │           │
│   │   │           ├── entities/   <-- Các đối tượng dữ liệu (Models)
│   │   │           │   ├── TourLocation.java  (Điểm tham quan)
│   │   │           │   └── Customer.java      (Khách hàng)
│   │   │           │
│   │   │           ├── structures/ <-- QUAN TRỌNG NHẤT: Code CTDL tự viết 
│   │   │           │   ├── MyLinkedList.java  (Quản lý Tour [cite: 35])
│   │   │           │   ├── MyBST.java         (Quản lý Customer [cite: 37])
│   │   │           │   ├── MyGraph.java       (Bản đồ & Dijkstra [cite: 38])
│   │   │           │   └── Node.java          (Node cơ bản dùng chung)
│   │   │           │
│   │   │           └── utils/      <-- Tiện ích hỗ trợ
│   │   │               ├── FileLoader.java    (Đọc file text bản đồ [cite: 84])
│   │   │               └── InputValidator.java
│   │   │
│   │   └── resources/              <-- Nơi chứa file Web & Data
│   │       ├── data/
│   │       │   ├── map_data.txt       (Dữ liệu bản đồ đầu vào)
│   │       │   └── customers.txt
│   │       │
│   │       └── public/             <-- FRONTEND (HTML/CSS/JS) để ở đây
│   │           ├── index.html
│   │           ├── style.css
│   │           ├── script.js
│   │           └── assets/ (ảnh, icon)
│   │
│   └── test/                       <-- BẮT BUỘC: Nơi viết Unit Test 
│       └── java/
│           └── com/
│               └── travelplanner/
│                   └── structures/
│                       ├── MyLinkedListTest.java
│                       └── MyBSTTest.java



Promt: bạn dựa vào cái doc của tôi bạn hãy code cho tôi theo kế hoạch tuần 1 đến tuần 3

📅 Tuần 1: Xây dựng dữ liệu nền (Entities & Node)
Tuần này làm nhẹ nhàng thôi, chủ yếu là định nghĩa xem "một địa điểm du lịch" gồm những gì.

1. File TourLocation.java (Gói entities) Đây là đối tượng chứa thông tin địa điểm (Hà Nội, Đà Nẵng...).

Java

package com.travelplanner.entities;

public class TourLocation {
    private String id;
    private String name;
    private String description;
    private double price;

    public TourLocation(String id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // Getter & Setter (Bắt buộc để lấy dữ liệu ra)
    public String getId() { return id; }
    public String getName() { return name; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s - %.2f VND", id, name, price);
    }
    
    // Hàm so sánh để sau này tìm kiếm/xóa (quan trọng)
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TourLocation that = (TourLocation) obj;
        return id.equals(that.id); // So sánh theo ID
    }
}
2. File Node.java (Gói structures)  Bạn dùng lại class Node<T> generic mình đã gửi ở bước trước. Nó đóng vai trò là "toa tàu" chở TourLocation.

📅 Tuần 2: Code lõi Linked List (Core Logic)
Đây là tuần căng nhất. Bạn phải tự viết class quản lý danh sách. Tuyệt đối KHÔNG import java.util.LinkedList nhé.


File MyLinkedList.java (Gói structures) 
+1

Java

package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;

public class MyLinkedList {
    private Node<TourLocation> head;
    private int size;

    public MyLinkedList() {
        this.head = null;
        this.size = 0;
    }

    // 1. Thêm địa điểm vào cuối hành trình (Add Last)
    public void addLocation(TourLocation location) {
        Node<TourLocation> newNode = new Node<>(location);
        if (head == null) {
            head = newNode;
        } else {
            Node<TourLocation> current = head;
            while (current.next != null) { // Duyệt đến cuối danh sách
                current = current.next;
            }
            current.next = newNode; // Nối đuôi
        }
        size++;
    }

    // 2. Xóa địa điểm theo ID (Delete Node) [cite: 36]
    public boolean removeLocation(String locationId) {
        if (head == null) return false;

        // Nếu xóa thằng đầu tiên (Head)
        if (head.data.getId().equals(locationId)) {
            head = head.next;
            size--;
            return true;
        }

        // Xóa thằng ở giữa hoặc cuối
        Node<TourLocation> current = head;
        while (current.next != null) {
            if (current.next.data.getId().equals(locationId)) {
                current.next = current.next.next; // Cắt dây nối
                size--;
                return true;
            }
            current = current.next;
        }
        return false; // Không tìm thấy
    }

    // 3. In danh sách ra màn hình Console (để demo)
    public void printTour() {
        if (head == null) {
            System.out.println("Tour đang trống!");
            return;
        }
        Node<TourLocation> temp = head;
        System.out.print("START -> ");
        while (temp != null) {
            System.out.print(temp.data.getName() + " -> ");
            temp = temp.next;
        }
        System.out.println("END");
    }
    
    // Helper: Lấy kích thước
    public int size() { return size; }
}
📅 Tuần 3: Test & Debug (Unit Test)
Yêu cầu bắt buộc là phải có Unit Test để chứng minh code AI (hoặc code bạn viết) không bị lỗi NullPointerException.

1. File ConsoleApp.java (Gói app) - Chạy thử bằng mắt

Java

package com.travelplanner.app;

import com.travelplanner.entities.TourLocation;
import com.travelplanner.structures.MyLinkedList;

public class ConsoleApp {
    public static void main(String[] args) {
        MyLinkedList myTour = new MyLinkedList();

        // 1. Thêm địa điểm
        myTour.addLocation(new TourLocation("L01", "Hồ Gươm", "Trung tâm HN", 0));
        myTour.addLocation(new TourLocation("L02", "Vịnh Hạ Long", "Di sản", 500000));
        
        System.out.println("--- Hành trình ban đầu ---");
        myTour.printTour(); // Kỳ vọng: Hồ Gươm -> Vịnh Hạ Long -> END

        // 2. Xóa thử
        System.out.println("\n--- Sau khi xóa Hồ Gươm ---");
        myTour.removeLocation("L01");
        myTour.printTour(); // Kỳ vọng: Vịnh Hạ Long -> END
    }
}
2. File MyLinkedListTest.java (Gói test) - Test tự động (JUnit) Bạn tạo file này trong thư mục test (màu xanh lá). Đây là cái giúp bạn lấy điểm Progress Test.

Java

package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {
    
    @Test
    public void testAddAndRemove() {
        MyLinkedList list = new MyLinkedList();
        TourLocation loc1 = new TourLocation("1", "A", "Desc", 100);
        
        // Test case 1: Thêm vào list rỗng
        list.addLocation(loc1);
        assertEquals(1, list.size(), "Size phải là 1 sau khi thêm");
        
        // Test case 2: Xóa item có tồn tại
        boolean isDeleted = list.removeLocation("1");
        assertTrue(isDeleted, "Phải xóa thành công");
        assertEquals(0, list.size(), "List phải về 0 sau khi xóa");
    }

    @Test
    public void testRemoveFromEmpty() {
        // Test case 3: Xóa từ list rỗng (Kiểm tra NullPointer)
        MyLinkedList emptyList = new MyLinkedList();
        boolean result = emptyList.removeLocation("999");
        assertFalse(result, "Xóa list rỗng phải trả về false, không được crash!");
    }
}