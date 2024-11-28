// FoodDao.java
package com.example.calorietracker.data.local.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.calorietracker.data.local.entities.Food;

import java.util.List;

@Dao
public interface FoodDao {
    @Insert
    void insert(Food food);

    @Query("SELECT * FROM food_table WHERE name = :name LIMIT 1")
    LiveData<Food> getFoodByName(String name);

    @Query("SELECT * FROM food_table WHERE name = :name LIMIT 1")
    Food getFoodByNameSync(String name); // Synchronous query to check existence

    @Query("SELECT * FROM food_table")
    LiveData<List<Food>> getAllFoods();

    @Query("DELETE FROM food_table")
    void deleteAll();
}

