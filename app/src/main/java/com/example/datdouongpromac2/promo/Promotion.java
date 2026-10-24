package com.example.datdouongpromac2.promo;

public class Promotion {
    private int id;
    private String code;
    private String description;
    private int discountPercent;
    private String startDate;
    private String endDate;
    private boolean isActive;

    public Promotion(int id, String code, String description, int discountPercent,
                     String startDate, String endDate, boolean isActive) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public int getDiscountPercent() { return discountPercent; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public boolean isActive() { return isActive; }

    public void setId(int id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setDescription(String description) { this.description = description; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setActive(boolean active) { isActive = active; }
}
