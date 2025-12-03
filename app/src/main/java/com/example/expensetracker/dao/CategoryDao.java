package com.example.expensetracker.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.expensetracker.model.Category;
import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    List<Category> getAllCategoriesSync();

    @Query("SELECT * FROM categories WHERE name = :name OR nameKm = :name LIMIT 1")
    Category getCategoryByName(String name);

    // Optional: Update if you need to update Khmer name
    @Query("UPDATE categories SET nameKm = :nameKm WHERE id = :id")
    void updateKhmerName(int id, String nameKm);
}