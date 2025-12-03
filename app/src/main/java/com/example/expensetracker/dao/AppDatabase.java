package com.example.expensetracker.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.example.expensetracker.model.Category;

@Database(entities = {Category.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
}