package com.example.weddingplanner;

public class Event {
    private String id;
    private String name;
    private String date;
    private String place;
    private String time;
    private String description;
    private String managerId;
    private String customerId;
    private String budgetRange;
    private  String paymentStatus;

    public Event(){

    }
    public Event(String id, String customerId, String managerId, String date, String time, String place, String description, String budgetRange, String paymentStatus) {
        this.id = id;
        this.customerId = customerId;
        this.managerId = managerId;
        this.date = date;
        this.time = time;
        this.place = place;
        this.description = description;
        this.budgetRange = budgetRange;
        this.paymentStatus = paymentStatus;
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

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public String getDescription() {return description;}
    public String getManagerId() {return managerId;}
    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }
    public String getBudgetRange() {return budgetRange;}
    public String getPaymentStatus() {return paymentStatus;}
    public void setPaymentStatus(String paymentStatus) {this.paymentStatus = paymentStatus;}
    public String getCustomerId() {
        return customerId;
    }
}
