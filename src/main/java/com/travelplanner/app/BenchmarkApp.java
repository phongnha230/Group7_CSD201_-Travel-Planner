package com.travelplanner.app;

import java.util.ArrayList;

import com.travelplanner.entities.Customer;
import com.travelplanner.structures.MyBST; // Dòng này sẽ báo đỏ nếu chưa có file Seeder
import com.travelplanner.utils.DataSeeder;

public class BenchmarkApp {
    public static void main(String[] args) {
        System.out.println("=== CHUẨN BỊ DỮ LIỆU ĐUA XE ===");
        int N = 10000; // Số lượng khách hàng (10k)
        
        // 1. Khởi tạo cấu trúc
        MyBST myTree = new MyBST();
        ArrayList<Customer> myList = new ArrayList<>(); // Đại diện cho Linked List

        // 2. Nạp dữ liệu (Bước này cần DataSeeder)
        // Nếu chưa có file DataSeeder, 2 dòng dưới này sẽ báo lỗi đỏ
        DataSeeder.seedBST(myTree, N);
        DataSeeder.seedList(myList, N);

        // ID của người cuối cùng (Trường hợp xấu nhất để test tốc độ)
        String searchId = "CUS" + (N - 1); 

        System.out.println("\n=== BẮT ĐẦU ĐUA TỐC ĐỘ (BENCHMARK) ===");
        System.out.println("Đang tìm khách hàng: " + searchId);

        // --- ĐUA VÒNG 1: LINKED LIST (ArrayList) ---
        long startTime = System.nanoTime();
        boolean foundInList = false;
        for (Customer c : myList) {
            if (c.getId().equals(searchId)) {
                foundInList = true;
                break;
            }
        }
        long listTime = System.nanoTime() - startTime;
        System.out.println("Linked List (Duyệt tuần tự) mất: " + listTime + " ns");
        System.out.println("Tìm thấy trong List: " + foundInList);

        // --- ĐUA VÒNG 2: BST (Cây nhị phân) ---
        startTime = System.nanoTime();
        Customer foundInTree = myTree.search(searchId);
        long treeTime = System.nanoTime() - startTime;
        System.out.println("BST (Tìm kiếm nhị phân) mất:   " + treeTime + " ns");
        System.out.println("Tìm thấy trong BST: " + (foundInTree != null));

        // --- TỔNG KẾT ---
        if (treeTime > 0) { // Tránh chia cho 0
            long speedUp = listTime / treeTime;
            System.out.println("=> KẾT LUẬN: BST nhanh gấp " + speedUp + " lần so với Linked List!");
        } else {
            System.out.println("=> BST quá nhanh, không đo được bằng nano giây!");
        }
    }
}