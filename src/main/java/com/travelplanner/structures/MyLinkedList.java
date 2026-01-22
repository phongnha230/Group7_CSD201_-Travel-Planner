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
        if (location == null) return; // Prevent adding nulls

        Node<TourLocation> newNode = new Node<>(location);
        
        if (head == null) {
            // Trường hợp list rỗng: Đầu và Đuôi là một
            head = newNode;
            tail = newNode;
        } else {
            // Trường hợp có dữ liệu: Chỉ cần nối vào đuôi cũ
            tail.next = newNode;
            tail = newNode; // Cập nhật đuôi mới
        }
        size++;
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