package com.travelplanner.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.travelplanner.entities.Customer;

public class MyBSTTest {

    // Helper tạo nhanh khách hàng
    private Customer createCus(String id) {
        return new Customer(id, "User " + id, "09090909", "email@test.com");
    }

    @Test
    public void testInsertAndSearch() {
        MyBST tree = new MyBST();
        
        // Chèn lộn xộn để xem cây có tự sắp xếp không
        tree.insert(createCus("C05")); // Gốc
        tree.insert(createCus("C03")); // Trái
        tree.insert(createCus("C08")); // Phải
        tree.insert(createCus("C01")); // Trái của Trái

        // Kiểm tra count
        assertEquals(4, tree.count());

        // Test tìm thấy
        Customer found = tree.search("C08");
        assertNotNull(found);
        assertEquals("User C08", found.getName()); // Sửa getName() thành getter tương ứng trong Customer của bạn

        // Test tìm không thấy
        Customer notFound = tree.search("C99");
        assertNull(notFound);
    }

    @Test
    public void testDeleteLeafNode() {
        // Xóa node lá (Node không có con) - Dễ nhất
        MyBST tree = new MyBST();
        tree.insert(createCus("C10"));
        tree.insert(createCus("C05")); // Node lá

        tree.delete("C05");

        assertNull(tree.search("C05"));
        assertEquals(1, tree.count());
    }

    @Test
    public void testDeleteNodeWithOneChild() {
        // Xóa node có 1 con
        MyBST tree = new MyBST();
        tree.insert(createCus("C10"));
        tree.insert(createCus("C05"));
        tree.insert(createCus("C03")); // C03 là con của C05

        // Xóa C05 -> C03 phải nhảy lên thế chỗ
        tree.delete("C05");

        assertNull(tree.search("C05"));
        assertNotNull(tree.search("C03")); // C03 vẫn phải còn
        assertEquals(2, tree.count());
    }

    @Test
    public void testDeleteNodeWithTwoChildren() {
        // CA ĐẠI PHẪU: Xóa node có 2 con (Khó nhất)
        MyBST tree = new MyBST();
        tree.insert(createCus("C10")); // Gốc
        tree.insert(createCus("C05")); // Node cần xóa
        tree.insert(createCus("C02")); // Con trái
        tree.insert(createCus("C08")); // Con phải

        // Xóa C05 -> Theo logic code trước: C08 (nhỏ nhất bên phải... à nhầm, logic là nhỏ nhất bên phải của nhánh phải)
        // Trong trường hợp này C05 có con phải là C08 (lá). 
        // Logic chuẩn: Lấy min của bên phải thế vào.
        
        tree.delete("C05");

        assertNull(tree.search("C05"));
        assertNotNull(tree.search("C02"));
        assertNotNull(tree.search("C08"));
        assertEquals(3, tree.count());
    }
    
    @Test
    public void testDeleteRoot() {
        // Thử thách cực đại: Xóa luôn Node Gốc (Root)
        MyBST tree = new MyBST();
        tree.insert(createCus("C10"));
        tree.insert(createCus("C20"));
        
        tree.delete("C10"); // Xóa gốc
        
        assertNull(tree.search("C10"));
        assertNotNull(tree.search("C20"));
        assertEquals(1, tree.count());
    }
}