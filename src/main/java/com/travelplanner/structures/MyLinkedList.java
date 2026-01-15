package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;

public class MyLinkedList {
    private Node<TourLocation> head;
    private int size;

    public MyLinkedList(){
        this.head = null;
        this.size = 0;
    }

    //1.Thêm địa điểm vào cuối hành trình (Add Last)
    public void addLocation(TourLocation location) {
        Node<TourLocation> newNode = new Node<>(location);
        if (head == null) {
            head = newNode;
        } else {
            Node<TourLocation> current = head;
            while (current.next != null) {//Duyệt đến cuối danh sách
                current = current.next;
            }
            current.next = newNode;//Nối đuôi
        }
        size++ ;
        }

    //2.Xóa địa điểm theo ID(Delete Node) [cite: 36]
    public boolean removeLocation(String locationld) {
        if (head == null) return false;

        //Nếu xóa đầu tiên (Head)
        if (head.data.getId().equals(locationld)) {
            head = head.next;
            size--;
            return true;
        }

        Node<TourLocation> current = head;
        while (current.next != null) {
            if (current.next.data.getId().equals(locationld)) {
                current.next = current.next.next;//Cắt dây nối
                size--;
                return true;
                }
            current = current.next;
        }
        return false; 
        }
    public void printTour() {
        if (head == null) {
            System.out.printf("Tour đang trống!");
            return;
        }
        Node<TourLocation> temp = head;
        System.out.print("START -> ");
        while (temp != null) {
            System.err.print(temp.data.getName() + " -> ");
            temp = temp.next;
        }
        System.out.println("END");
    }

    //Helper: Lấy kích thước
    public int size() {return size;}
}
