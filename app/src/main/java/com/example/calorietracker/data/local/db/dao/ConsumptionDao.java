package com.example.calorietracker.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.calorietracker.data.local.entities.Consumption;

@Dao
public interface ConsumptionDao {
    @Insert
    void insert(Consumption consumption);
    @Query("DELETE FROM consumption_table WHERE date = :date")
    void deleteConsumptionForDate(String date);

    @Query("SELECT * FROM consumption_table WHERE date = :date LIMIT 1")
    Consumption getConsumptionByDate(String date);

    @Query("UPDATE consumption_table SET totalCalories = :calories, totalProtein = :protein WHERE date = :date")
    void updateConsumption(String date, float calories, float protein);

    @Query("DELETE FROM consumption_table")
    void deleteAll();
}