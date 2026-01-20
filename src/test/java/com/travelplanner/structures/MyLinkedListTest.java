package com.travelplanner.structures;

import com.travelplanner.entities.TourLocation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MyLinkedListTest {
    
    @Test
    public void testAddAndRemove() {
        MyLinkedList list = new MyLinkedList();
        TourLocation loc1 = new TourLocation("1", "A", "Desc", 100);
        
        // Test case 1: Thêm vào list rỗng
        list.addLocation(loc1);
        assertEquals(1, list.size(), "Size phải là 1 sau khi thêm");
        
        // Test case 2: Xóa item có tồn tại
        boolean isDeleted = list.removeLocation("1");
        assertTrue(isDeleted, "Phải xóa thành công");
        assertEquals(0, list.size(), "List phải về 0 sau khi xóa");
    }

    @Test
    public void testRemoveFromEmpty() {
        // Test case 3: Xóa từ list rỗng (Kiểm tra NullPointer)
        MyLinkedList emptyList = new MyLinkedList();
        boolean result = emptyList.removeLocation("999");
        assertFalse(result, "Xóa list rỗng phải trả về false, không được crash!");
    }
}