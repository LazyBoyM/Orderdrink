package com.example.datdouongpromac2.cart;

import com.example.datdouongpromac2.topping.Topping;
import java.util.ArrayList;

public class CartItem {
    private String name;
    private long price;
    private int quantity;
    private ArrayList<Topping> toppings;
    public CartItem(String name, long price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.toppings = new ArrayList<>();
    }

    public String getName() { return name; }
    public long getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public ArrayList<Topping> getToppings() { return toppings; }

    public void setName(String name) { this.name = name; }
    public void setPrice(long price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }


    public long getTotalPrice() {
        long total = price * quantity;
        for (Topping topping : toppings) {
            if (topping.isSelected()) {
                total += topping.getPrice() * quantity;
            }
        }
        return total;
    }


    public void addTopping(Topping topping) {
        toppings.add(topping);
    }


    public void removeTopping(Topping topping) {
        toppings.remove(topping);
    }
}
