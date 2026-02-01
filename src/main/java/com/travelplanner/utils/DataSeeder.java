package com.travelplanner.utils;

import java.util.List;

import com.travelplanner.entities.Customer;
import com.travelplanner.structures.MyBST;

public class DataSeeder {
    
    // Nạp dữ liệu vào Cây BST
    public static void seedBST(MyBST tree, int count) {
        System.out.println("dang sinh " + count + " khach hang vao Tree...");
        for (int i = 0; i < count; i++) {
            String id = "CUS" + i;
            // Fix: Thêm email giả vào
            String email = "customer" + i + "@email.com"; 
            
            tree.insert(new Customer(id, "Khach " + i, "090" + i, email));
        }
    }

    // Nạp dữ liệu vào List (Dùng Java List hoặc MyLinkedList của bạn đều được)
    public static void seedList(List<Customer> list, int count) {
        System.out.println("dang sinh " + count + " khach hang vao List...");
        for (int i = 0; i < count; i++) {
            String id = "CUS" + i;
            // Fix: Thêm email giả vào
            String email = "customer" + i + "@email.com";
            
            list.add(new Customer(id, "Khach " + i, "090" + i, email));
        }
    }
}