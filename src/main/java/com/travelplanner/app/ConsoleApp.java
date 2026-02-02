package com.travelplanner.app;

import com.travelplanner.entities.TourLocation;
import com.travelplanner.structures.MyLinkedList;

public class ConsoleApp {
    public static void main(String[] args) {
        MyLinkedList<TourLocation> myTour = new MyLinkedList<>();

        // 1. Add locations
        myTour.add(new TourLocation("L01", "Hoan Kiem Lake", "Hanoi City Center", 0));
        myTour.add(new TourLocation("L02", "Ha Long Bay", "World Heritage Site", 500000));
        
        System.out.println("--- Initial Itinerary ---");
        myTour.printList();

        // 2. Test removal
        System.out.println("\n--- After removing Hoan Kiem Lake ---");
        myTour.removeById("L01");
        myTour.printList();
    }
}
