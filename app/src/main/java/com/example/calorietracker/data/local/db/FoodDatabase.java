// FoodDatabase.java
package com.example.calorietracker.data.local.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.calorietracker.data.local.db.dao.ConsumptionDao;
import com.example.calorietracker.data.local.db.dao.FoodDao;
import com.example.calorietracker.data.local.entities.Consumption;
import com.example.calorietracker.data.local.entities.Food;

@Database(entities = {Food.class, Consumption.class}, version = 3, exportSchema = false)
public abstract class FoodDatabase extends RoomDatabase {

    private static FoodDatabase INSTANCE;

    public abstract FoodDao foodDao();
    public abstract ConsumptionDao consumptionDao(); // Added this line

    public static synchronized FoodDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FoodDatabase.class, "food_database")
                    .fallbackToDestructiveMigration() // This allows for data loss when migrating
                    .build();
        }
        return INSTANCE;
    }
}
