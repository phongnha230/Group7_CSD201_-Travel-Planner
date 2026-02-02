package com.travelplanner.app;

import com.travelplanner.entities.Customer;
import com.travelplanner.structures.MyBST;
import com.travelplanner.structures.MyLinkedList;
import com.travelplanner.utils.DataSeeder;

public class BenchmarkApp {
    public static void main(String[] args) {
        System.out.println("=== CHUẨN BỊ DỮ LIỆU ĐUA XE ===");
        int N = 10000; // Số lượng khách hàng (10k)
        
        // 1. Khởi tạo cấu trúc
        MyBST myTree = new MyBST();
        MyLinkedList<Customer> myList = new MyLinkedList<>();

        // 2. Nạp dữ liệu
        DataSeeder.seedBST(myTree, N);
        // We need to update DataSeeder or just use a loop here since seedList expects java.util.List
        for (int i = 0; i < N; i++) {
            String id = "CUS" + i;
            String email = "customer" + i + "@email.com";
            myList.add(new Customer(id, "Khach " + i, "090" + i, email));
        }

        // ID của người cuối cùng (Trường hợp xấu nhất để test tốc độ)
        String searchId = "CUS" + (N - 1); 

        System.out.println("\n=== BẮT ĐẦU ĐUA TỐC ĐỘ (BENCHMARK) ===");
        System.out.println("Đang tìm khách hàng: " + searchId);

        // --- ĐUA VÒNG 1: MY LINKED LIST ---
        long startTime = System.nanoTime();
        Customer foundInList = myList.searchById(searchId);
        long listTime = System.nanoTime() - startTime;
        System.out.println("MyLinkedList (Duyệt tuần tự) mất: " + listTime + " ns");
        System.out.println("Tìm thấy trong List: " + (foundInList != null));

        // --- ĐUA VÒNG 2: BST (Cây nhị phân) ---
        startTime = System.nanoTime();
        Customer foundInTree = myTree.search(searchId);
        long treeTime = System.nanoTime() - startTime;
        System.out.println("BST (Tìm kiếm nhị phân) mất:   " + treeTime + " ns");
        System.out.println("Tìm thấy trong BST: " + (foundInTree != null));

        // --- TỔNG KẾT ---
        if (treeTime > 0) {
            long speedUp = listTime / treeTime;
            System.out.println("=> KẾT LUẬN: BST nhanh gấp " + speedUp + " lần so với MyLinkedList!");
        } else {
            System.out.println("=> BST quá nhanh, không đo được bằng nano giây!");
        }
    }
}
