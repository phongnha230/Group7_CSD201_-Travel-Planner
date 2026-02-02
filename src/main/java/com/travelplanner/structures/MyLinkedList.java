package com.travelplanner.structures;

import com.travelplanner.entities.Identifiable;

public class MyLinkedList<T extends Identifiable> {
    private Node<T> head; // Đầu tàu
    private Node<T> tail; // Đuôi tàu (Nâng cấp quan trọng!)
    private int size;

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // 1. Thêm vào cuối (Đã tối ưu O(1) nhờ biến tail)
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        
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
    public boolean removeById(String id) {
        if (head == null) return false;

        // TH1: Xóa ngay thằng đầu tiên (Head)
        if (head.data.getId().equals(id)) {
            head = head.next;
            size--;
            
            // Nếu xóa xong mà list rỗng luôn -> Phải reset cả tail về null
            if (head == null) {
                tail = null;
            }
            return true;
        }

        // TH2: Xóa ở giữa hoặc cuối
        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.getId().equals(id)) {
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
    public T get(int index) {
        if (index < 0 || index >= size) return null;
        
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    // 4. In danh sách (Hỗ trợ debug)
    public void printList() {
        if (head == null) {
            System.out.println("Danh sách trống!");
            return;
        }
        Node<T> temp = head;
        System.out.print("START -> ");
        while (temp != null) {
            System.out.print(temp.data.toString() + " -> ");
            temp = temp.next;
        }
        System.out.println("END");
    }

    // 5. Chuyển thành mảng (Dùng để gửi JSON ra Web sau này)
    public Object[] toArray() {
        Object[] arr = new Object[size];
        Node<T> current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = current.data;
            current = current.next;
        }
        return arr;
    }

    // 6. Tìm kiếm theo ID (O(n))
    public T searchById(String id) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.getId().equals(id)) {
                return current.data;
            }
            current = current.next;
        }
        return null;
    }

    // Helper: Lấy kích thước
    public int size() { return size; }
}
