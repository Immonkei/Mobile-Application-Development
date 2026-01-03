package com.example.expensetracker.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Expense implements Serializable {

    @SerializedName("id")
    private String id;                // server uses GUID

    @SerializedName("amount")
    private double amount;

    @SerializedName("currency")
    private String currency;

    // Use 'category' and 'remark' to match server naming
    @SerializedName("category")
    private String category;

    @SerializedName("remark")
    private String remark;

    @SerializedName("createdBy")
    private String createdBy;

    // ISO-8601 date/time as string (e.g. 2025-11-03T14:23:00Z)
    @SerializedName("createdDate")
    private String createdDate;

    @SerializedName("receiptImageUrl")
    private String receiptImageUrl;


    // default constructor (Gson)
    public Expense() {
    }

    public Expense(String id, double amount, String currency, String category, String remark, String createdBy, String createdDate,String receiptImageUrl
    ) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.category = category;
        this.remark = remark;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.receiptImageUrl= receiptImageUrl;
    }

    // convenience constructor for creating new expense (id & createdDate may be generated client-side)
    public Expense(double amount, String currency, String category, String remark, String createdBy, String createdDate ,String receiptImageUrl) {
        this(null, amount, currency, category, remark, createdBy, createdDate ,receiptImageUrl);
    }

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public String getReceiptImageUrl() {
        return receiptImageUrl;
    }
    public void setReceiptImageUrl(String receiptImageUrl) {
        this.receiptImageUrl = receiptImageUrl;
    }

}
