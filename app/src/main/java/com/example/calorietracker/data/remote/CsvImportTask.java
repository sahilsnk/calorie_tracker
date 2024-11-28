// CsvImportTask.java
package com.example.calorietracker.data.remote;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.calorietracker.data.local.db.FoodDatabase;
import com.example.calorietracker.data.local.entities.Food;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvImportTask extends AsyncTask<Void, Void, Void> {
    private Context context;

    public CsvImportTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FoodDatabase db = FoodDatabase.getDatabase(context);

        try {
            InputStream is = context.getAssets().open("food_data.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");

                if (columns.length < 3) {
                    Log.w("CsvImportTask", "Skipping invalid line: " + line);
                    continue; // Skip lines that don't have enough columns
                }

                String name = columns[0].trim();
                float calories = parseFloatSafe(columns[1].trim());
                float protein = parseFloatSafe(columns[2].trim());

                // Check if the food item already exists
                Food existingFood = db.foodDao().getFoodByNameSync(name);
                if (existingFood == null) {
                    Food food = new Food(name, calories, protein);
                    db.foodDao().insert(food);
                    Log.d("CsvImportTask", "Inserted food: " + name);
                } else {
                    Log.d("CsvImportTask", "Skipped inserting duplicate food: " + name);
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("CsvImportTask", "Error reading CSV file", e);
        }
        return null;
    }

    private float parseFloatSafe(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            Log.w("CsvImportTask", "Skipping invalid number format: " + value);
            return 0;
        }
    }


}