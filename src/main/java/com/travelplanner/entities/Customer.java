package com.travelplanner.entities;

public class Customer implements Comparable<Customer> {
    private String id;      // Key tìm kiếm
    private String name;
    private String phone;
    private String email;   // <--- Mới thêm

    // Nhớ cập nhật Constructor nhận thêm email
    public Customer(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    

    public String getId() { return id; }
    public String getEmail() { return email; } // Getter mới

    @Override
    public String toString() {
        // In ra đầy đủ nhìn cho chuyên nghiệp
        return String.format("[%s] %s | SĐT: %s | Email: %s", id, name, phone, email);
    }

    // Hàm này KHÔNG ĐỔI (Vẫn so sánh theo ID)
    @Override
    public int compareTo(Customer other) {
        return this.id.compareTo(other.id);
    }
}