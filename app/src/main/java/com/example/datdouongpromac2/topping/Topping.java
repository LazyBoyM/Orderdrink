package com.example.datdouongpromac2.topping;

public class Topping {
    private int id;
    private String name;
    private double price;
    private boolean isSelected;

    public Topping(int id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isSelected = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public boolean isSelected() { return isSelected; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
