package com.example.expensetracker.model;

public class Expense {
    private final int id;
    private final double amount;
    private final String currency;
    private final String date; // <-- The private field for the date
    private final String category;
    private final String remark;

    // Your constructor is likely here
    public Expense(int id, double amount, String currency, String date, String category, String remark) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.date = date;
        this.category = category;
        this.remark = remark;
    }

    // --- You probably have these getters already ---
    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCategory() {
        return category;
    }

    public String getRemark() {
        return remark;
    }

    // =======================================================
    // ========== THIS IS THE FIX: ADD THIS METHOD ===========
    // =======================================================
    public String getDate() {
        return date;
    }
    // =======================================================

}
