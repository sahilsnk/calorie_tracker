package com.example.calorietracker.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "consumption_table")
public class Consumption {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String date;
    private float totalCalories;
    private float totalProtein;

    public Consumption(String date, float totalCalories, float totalProtein) {
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(float totalCalories) {
        this.totalCalories = totalCalories;
    }

    public float getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(float totalProtein) {
        this.totalProtein = totalProtein;
    }
}
