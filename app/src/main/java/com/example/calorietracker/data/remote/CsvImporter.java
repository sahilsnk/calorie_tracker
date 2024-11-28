// CsvImporter.java
package com.example.calorietracker.data.remote;

import android.content.Context;
import android.util.Log;

import com.example.calorietracker.data.local.db.FoodDatabase;
import com.example.calorietracker.data.local.entities.Food;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CsvImporter {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void importCsv(Context context) {
        executor.execute(() -> {
            try {
                InputStream is = context.getAssets().open("food_data.csv");
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                FoodDatabase db = FoodDatabase.getDatabase(context);

                while ((line = reader.readLine()) != null) {
                    String[] columns = line.split(",");
                    String name = columns[0];
                    float calories = Float.parseFloat(columns[1]);
                    float protein = Float.parseFloat(columns[2]);

                    Food food = new Food(name, calories, protein);
                    db.foodDao().insert(food);
                }
                reader.close();
            } catch (IOException e) {
                Log.e("CsvImporter", "Error reading CSV file", e);
            }
        });
    }
}
