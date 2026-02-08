package com.travelplanner.entities;

public class TourLocation {
    private String id;
    private String name;
    private String description;
    private double price;
    private int x; // Toạ độ X trên bản đồ UI
    private int y; // Toạ độ Y trên bản đồ UI
    private String imageUrl; // URL ảnh upload cho itinerary item

    public TourLocation(String id, String name, String description, double price, int x, int y) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.x = x;
        this.y = y;
        this.imageUrl = null;
    }

    // Constructor cũ để giữ tương thích (mặc định x=0, y=0)
    public TourLocation(String id, String name, String description, double price) {
        this(id, name, description, price, 0, 0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "TourLocation [id=" + id + ", name=" + name + ", description=" + description + ", price=" + price
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        TourLocation that = (TourLocation) obj;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
