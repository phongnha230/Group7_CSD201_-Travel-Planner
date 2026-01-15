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