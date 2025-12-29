package com.example.mal2017_assessmentmodule.models;

/**
 * MenuItem Model - Represents a menu item in the restaurant.
 *
 * Features:
 * - Item details (name, description, price)
 * - Category classification
 * - Image URL for visual display
 * - Availability status
 *
 * @author BSCS2509254
 * @version 1.0
 */
public class MenuItem {

    private int itemId;
    private String name;
    private String description;
    private double price;
    private String category;  // Appetizers, Main Course, Desserts, Beverages
    private String imageUrl;
    private boolean available;

    public MenuItem() {}

    public MenuItem(String name, String description, double price, String category,
                    String imageUrl, boolean available) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.available = available;
    }

    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    /**
     * Format price for display (e.g., "RM 15.99")
     */
    public String getFormattedPrice() {
        return String.format("RM %.2f", price);
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "itemId=" + itemId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                '}';
    }
}