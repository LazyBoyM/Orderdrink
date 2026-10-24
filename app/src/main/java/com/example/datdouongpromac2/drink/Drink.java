package com.example.datdouongpromac2.drink;

public class Drink {
    private int id;
    private String name;
    private double price;
    private String description;
    private String category;
    private String image;  // ← THÊM - Tên drawable
    private int toppingCount;


    public Drink(int id, String name, double price, String description, String category, String image, int toppingCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.image = image;
        this.toppingCount = toppingCount;
    }


    public Drink(int id, String name, double price, String description, String category, String image) {
        this(id, name, price, description, category, image, 0);
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {  // ← THÊM NÀY
        return image;
    }

    public int getToppingCount() {
        return toppingCount;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImage(String image) {  // ← THÊM NÀY
        this.image = image;
    }

    public void setToppingCount(int toppingCount) {
        this.toppingCount = toppingCount;
    }

    @Override
    public String toString() {
        return name;
    }
}
