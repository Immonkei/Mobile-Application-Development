package com.example.expensetracker.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String nameKm; // Add this field

    // Constructors
    public Category() {
    }

    public Category(String name) {
        this.name = name;
        this.nameKm = name; // Initialize both
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameKm() {
        return nameKm;
    }

    public void setNameKm(String nameKm) {
        this.nameKm = nameKm;
    }
}