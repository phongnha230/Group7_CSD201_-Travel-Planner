package com.travelplanner.structures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.travelplanner.entities.TourLocation;

public class MyLinkedListTest {

    // Helper tạo nhanh địa điểm để đỡ gõ nhiều
    private TourLocation createLoc(String id) {
        return new TourLocation(id, "Loc " + id, "Desc", 100);
    }

    @Test
    public void testAddAndGet() {
        MyLinkedList list = new MyLinkedList();
        list.addLocation(createLoc("A"));
        list.addLocation(createLoc("B"));
        list.addLocation(createLoc("C"));

        // Kiểm tra size và thứ tự
        assertEquals(3, list.size());
        assertEquals("A", list.get(0).getId());
        assertEquals("B", list.get(1).getId());
        assertEquals("C", list.get(2).getId()); // Kiểm tra thằng cuối cùng
    }

    @Test
    public void testRemoveHead() {
        // Test xóa đầu: A -> B -> C xóa A còn B -> C
        MyLinkedList list = new MyLinkedList();
        list.addLocation(createLoc("A"));
        list.addLocation(createLoc("B"));

        boolean isDeleted = list.removeLocation("A");
        
        assertTrue(isDeleted);
        assertEquals(1, list.size());
        assertEquals("B", list.get(0).getId()); // B phải lên làm đầu
    }

    @Test
    public void testRemoveTail() {
        // CỰC KỲ QUAN TRỌNG: Test xem biến TAIL có cập nhật đúng ko
        MyLinkedList list = new MyLinkedList();
        list.addLocation(createLoc("A"));
        list.addLocation(createLoc("B"));
        list.addLocation(createLoc("C"));

        // Xóa thằng cuối (C)
        boolean isDeleted = list.removeLocation("C");
        
        assertTrue(isDeleted);
        assertEquals(2, list.size());
        assertEquals("B", list.get(1).getId()); // Thằng cuối bây giờ phải là B

        // Thử thách: Thêm mới ngay sau khi xóa đuôi
        // Nếu biến tail bị lỗi, thằng D này sẽ không nối được vào sau B
        list.addLocation(createLoc("D"));
        
        assertEquals(3, list.size());
        assertEquals("D", list.get(2).getId()); // D phải nằm đúng ở cuối
    }

    @Test
    public void testRemoveOnlyElement() {
        // Test xóa phần tử duy nhất (List trở về rỗng)
        MyLinkedList list = new MyLinkedList();
        list.addLocation(createLoc("A"));

        list.removeLocation("A");
        
        assertEquals(0, list.size());
        
        // Thêm lại để chắc chắn list không bị hỏng (Head/Tail phải reset về null rồi mới thêm lại)
        list.addLocation(createLoc("New"));
        assertEquals(1, list.size());
        assertEquals("New", list.get(0).getId());
    }
    
    @Test
    public void testRemoveNonExistent() {
        // Test xóa cái không có
        MyLinkedList list = new MyLinkedList();
        list.addLocation(createLoc("A"));
        
        boolean isDeleted = list.removeLocation("Z"); // Không có Z
        assertFalse(isDeleted);
        assertEquals(1, list.size());
    }
}