package com.example.weddingplanner;

public class Manager {
    private String id;
    private String name;
    private String budgetRange;
    private String ratings;
    private String previousEvents;
    public Manager() {
    }
    public Manager(String name, String budgetRange, String ratings, String previousEvents) {
        this.name = name;
        this.budgetRange = budgetRange;
        this.ratings = ratings;
        this.previousEvents = previousEvents;
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
    public String getBudgetRange() {
        return budgetRange;
    }
    public String getRatings() {
        return ratings;
    }
    public String getPreviousEvents() {
        return previousEvents;
    }
}
