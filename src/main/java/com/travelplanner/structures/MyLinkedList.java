package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;

public class MyLinkedList {
    private Node<TourLocation> head; // Đầu tàu
    private Node<TourLocation> tail; // Đuôi tàu (Nâng cấp quan trọng!)
    private int size;

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // 1. Thêm vào cuối (Đã tối ưu O(1) nhờ biến tail)
    public void addLocation(TourLocation location) {
        addAtTail(location);
    }

    // Thêm vào đầu (Head)
    public void addAtHead(TourLocation location) {
        Node<TourLocation> newNode = new Node<>(location);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    // Thêm vào cuối (Tail)
    public void addAtTail(TourLocation location) {
        Node<TourLocation> newNode = new Node<>(location);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // Thêm vào vị trí index (0 = đầu)
    public boolean addAtIndex(int index, TourLocation location) {
        if (index < 0 || index > size) return false;
        if (index == 0) {
            addAtHead(location);
            return true;
        }
        if (index == size) {
            addAtTail(location);
            return true;
        }
        Node<TourLocation> newNode = new Node<>(location);
        Node<TourLocation> current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }
        newNode.next = current.next;
        current.next = newNode;
        size++;
        return true;
    }

    // 2. Xóa theo ID (Xử lý kỹ các trường hợp đặc biệt)
    public boolean removeLocation(String locationId) {
        if (head == null) return false;

        // TH1: Xóa ngay thằng đầu tiên (Head)
        if (head.data.getId().equals(locationId)) {
            head = head.next;
            size--;
            
            // Nếu xóa xong mà list rỗng luôn -> Phải reset cả tail về null
            if (head == null) {
                tail = null;
            }
            return true;
        }

        // TH2: Xóa ở giữa hoặc cuối
        Node<TourLocation> current = head;
        while (current.next != null) {
            if (current.next.data.getId().equals(locationId)) {
                // Nếu thằng sắp xóa là thằng đuôi (Tail) -> Phải cập nhật lại tail
                if (current.next == tail) {
                    tail = current; // Thằng đứng trước nó lên làm đuôi
                }
                
                // Cắt dây nối (Bỏ qua thằng cần xóa)
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false; // Không tìm thấy
    }

    // 3. Lấy phần tử theo index (Dùng cho Unit Test)
    public TourLocation get(int index) {
        if (index < 0 || index >= size) return null;
        
        Node<TourLocation> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    // 4. In danh sách (Hỗ trợ debug)
    public void printTour() {
        if (head == null) {
            System.out.println("Tour trống!");
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
// 5. Chuyển thành mảng (Dùng để gửi JSON ra Web sau này)
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node<TourLocation> current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = current.data;
            current = current.next;
        }
        return arr;
    }

    // Helper: Lấy kích thước
    public int size() { return size; }
}  