package com.travelplanner.app;

import com.travelplanner.entities.TourLocation;
import com.travelplanner.structures.MyLinkedList;

public class ConsoleApp {
    public static void main(String[] args) {
        MyLinkedList myTour = new MyLinkedList();

        // 1. Add locations
        // L01: Hoan Kiem Lake, Hanoi City Center, Cost: 0
        myTour.addLocation(new TourLocation("L01", "Hoan Kiem Lake", "Hanoi City Center", 0));
        // L02: Ha Long Bay, World Heritage Site, Cost: 500,000
        myTour.addLocation(new TourLocation("L02", "Ha Long Bay", "World Heritage Site", 500000));
        
        System.out.println("--- Initial Itinerary ---");
        myTour.printTour(); // Expectation: Hoan Kiem Lake -> Ha Long Bay -> END

        // 2. Test removal
        System.out.println("\n--- After removing Hoan Kiem Lake ---");
        myTour.removeLocation("L01");
        myTour.printTour(); // Expectation: Ha Long Bay -> END
    }
}