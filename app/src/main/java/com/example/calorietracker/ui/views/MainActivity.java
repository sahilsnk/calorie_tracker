package com.example.calorietracker.ui.views;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.calorietracker.R;
import com.example.calorietracker.data.local.db.FoodDatabase;
import com.example.calorietracker.data.local.entities.Consumption;
import com.example.calorietracker.data.local.entities.Food;
import com.example.calorietracker.data.remote.CsvImportTask;
import com.example.calorietracker.ui.viewmodels.FoodViewModel;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView;
    private TextView textView;
    private TextView textDate;
    private TextView textProtein;
    private TextView textCalorie;
    private NumberPicker numberPicker;
    private FoodDatabase db;
    private FoodViewModel foodViewModel;
    private ArrayAdapter<String> adapter;
    private List<Food> foodList;
    private Button enButton;
    private Button rebutton;
    private Map<String, Float[]> temporaryStorage = new HashMap<>();
    private String temp;
    private float tempProtein;
    private float tempCalorie;
    private int totalcalories = 0;
    private int totalprotein = 0;
    private TableLayout tableLayout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "onCreate: Activity started");
        db = FoodDatabase.getDatabase(this);
        Log.d("MainActivity", "onCreate: Database initialized");
        new CsvImportTask(this).execute();
        Log.d("MainActivity", "onCreate: CSV import task executed");
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        enButton = findViewById((R.id.button1));
        rebutton = findViewById(R.id.button2);
        textDate = findViewById(R.id.textDate);
        textProtein = findViewById(R.id.textProtein);
        textCalorie = findViewById(R.id.textCalorie);
        textView = findViewById(R.id.textView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        numberPicker = findViewById(R.id.numberPicker);
        tableLayout = findViewById(R.id.tableLayout);  // Initialize TableLayout
        numberPicker.setEnabled(false);
        // Initialize the ViewModel
        foodViewModel = new ViewModelProvider(this).get(FoodViewModel.class);
        Log.d("MainActivity", "onCreate: ViewModel initialized");

        // Initialize the ArrayAdapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(adapter);
        Log.d("MainActivity", "onCreate: ArrayAdapter set");
        textDate.setText(date);
        // Observe the data from the ViewModel
        foodViewModel.getAllFoods().observe(this, new Observer<List<Food>>() {
            @Override
            public void onChanged(List<Food> foods) {
                Log.d("MainActivity", "onChanged: LiveData observer triggered");
                foodList = foods;
                updateAutoCompleteTextView();
                Log.d("MainActivity", "onChanged: AutoCompleteTextView updated");
            }
        });
        foodViewModel.getConsumptionForToday().observe(this, new Observer<Consumption>() {
            @Override
            public void onChanged(Consumption consumption) {
                if (consumption != null) {
                    totalcalories = Math.round(consumption.getTotalCalories());
                    totalprotein = Math.round(consumption.getTotalProtein());
                } else {
                    totalcalories = 0;
                    totalprotein = 0;
                }
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                textCalorie.setText("calories: " + totalcalories);
                textDate.setText(date); // Update with today's date
                textProtein.setText("protein: " + totalprotein);
            }
        });

        // Set up the AutoCompleteTextView listener
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            Log.d("MainActivity", "Selected item: " + selectedItem);
            numberPicker.setEnabled(true);
            displayFoodDetails(selectedItem);
        });
        rebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get today's date
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                // Reset daily consumption in ViewModel
                foodViewModel.resetDailyConsumption();

                // Clear the table layout
                tableLayout.removeAllViews();

                // Reset total calories and protein
                totalcalories = 0;
                totalprotein = 0;

                // Update the UI
                textCalorie.setText("calories: " + totalcalories);
                textDate.setText(date); // Update with today's date
                textProtein.setText("protein: " + totalprotein);

                // Optionally, reset other views if needed
                resetAutoCompleteTextView();
                numberPicker.setValue(1);

                // Show a Toast message
                Toast.makeText(MainActivity.this, "Daily consumption reset.", Toast.LENGTH_SHORT).show();
            }
        });



        enButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = autoCompleteTextView.getText().toString();
                int quantity = numberPicker.getValue();

                if (selectedItem.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please select a food item.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (foodList != null) {
                    boolean itemFound = false;
                    for (Food food : foodList) {
                        if (food.getName().equals(selectedItem)) {
                            float caloriesPer100g = food.getCalories();
                            float proteinPer100g = food.getProtein();

                            // Calculate based on the quantity
                            int totalCalories = Math.round((caloriesPer100g) * quantity);
                            int totalProtein = Math.round((proteinPer100g) * quantity);
                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                            // Update totalcalories and totalprotein here
                            totalcalories += totalCalories;
                            totalprotein += totalProtein;

                            // Add the data to the TableLayout
                            addRowToTable(selectedItem, totalCalories, totalProtein, quantity);
                            Toast.makeText(MainActivity.this, "Calories: " + totalCalories + " | Protein: " + totalProtein, Toast.LENGTH_SHORT).show();

                            updateTemp(totalProtein, totalCalories, food.getName()); // Update temp values
                            foodViewModel.insertOrUpdateConsumption(totalCalories, totalProtein);
                            resetAutoCompleteTextView();  // Reset the AutoCompleteTextView
                            textView.setText("select");
                            textCalorie.setText("calories: " + String.valueOf(totalcalories));
                            textDate.setText(date); // Assuming date is already a String
                            textProtein.setText("protein: " + String.valueOf(totalprotein));
                            numberPicker.setValue(1);
                            itemFound = true;
                            break;
                        }
                    }
                    if (!itemFound) {
                        Toast.makeText(MainActivity.this, "Food item not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Food list is empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set up the NumberPicker
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(10);
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            Log.d("MainActivity", "onValueChange: NumberPicker value changed to " + newVal);
            if(temp.contains("(")){
                textView.setText(newVal+" unit");
            }else{
                textView.setText(100*newVal+"gms");
            }

        });
    }

    private void updateTemp(float protein, float calorie, String name) {
        tempProtein = protein;
        tempCalorie = calorie;
        temp = name;
    }
    private void updateAutoCompleteTextView() {
        if (foodList != null && !foodList.isEmpty()) {
            Log.d("MainActivity", "Food list is loaded with " + foodList.size() + " items.");
            String[] foodNames = new String[foodList.size()];
            for (int i = 0; i < foodList.size(); i++) {
                foodNames[i] = foodList.get(i).getName();
            }
            adapter.clear();
            adapter.addAll(foodNames);
            adapter.notifyDataSetChanged();
        } else {
            Log.d("MainActivity", "Food list is empty or null.");
        }
    }

    private void resetAutoCompleteTextView() {
        autoCompleteTextView.setText("");
        if (autoCompleteTextView.isPopupShowing()) {
            autoCompleteTextView.dismissDropDown();
        }
    }

    private void displayFoodDetails(String foodName) {
        boolean itemFound = false;

        if (foodList != null) {
            for (Food food : foodList) {
                if (food.getName().equals(foodName)) {
                    Log.d("MainActivity", "Food Details - Calories: " + food.getCalories() + ", Protein: " + food.getProtein());
                    itemFound = true;
                    updateTemp(food.getProtein(), food.getCalories(), food.getName());
                    break;
                }
            }
        }

        if (!itemFound) {
            textView.setText("Food item not found.");
            Log.d("MainActivity", "Food item not found: " + foodName);
        }
    }
    private float parseFloatSafe(String value) {
        try {
            // Remove non-numeric characters except for decimal points
            String cleanedValue = value.replaceAll("[^0-9.]", "");
            return Float.parseFloat(cleanedValue);
        } catch (NumberFormatException e) {
            // Handle the error or provide a default value
            return 0.0f;
        }
    }
    private void addRowToTable(String foodName, int calories, int protein, int num) {
        TableRow tableRow = new TableRow(this);

        // Create a TextView for the food name
        TextView foodNameTextView = new TextView(this);
        foodNameTextView.setText(foodName);
        foodNameTextView.setTextSize(18);
        foodNameTextView.setPadding(16, 8, 16, 8);
        tableRow.addView(foodNameTextView);

        // Create a TextView for the calories
        TextView caloriesTextView = new TextView(this);
        caloriesTextView.setText(String.valueOf(calories));
        caloriesTextView.setTextSize(18);
        caloriesTextView.setPadding(16, 8, 16, 8);
        tableRow.addView(caloriesTextView);

        // Create a TextView for the protein
        TextView proteinTextView = new TextView(this);
        proteinTextView.setText(String.valueOf(protein));
        proteinTextView.setTextSize(18);
        proteinTextView.setPadding(16, 8, 16, 8);
        tableRow.addView(proteinTextView);

        // Create a TextView for the quantity
        TextView numValue = new TextView(this);
        numValue.setText(String.valueOf(num));
        numValue.setTextSize(18);
        numValue.setPadding(16, 8, 16, 8);
        tableRow.addView(numValue);

        // Create an ImageButton for deleting the row
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setImageResource(R.drawable.ic_delete);  // Use your icon resource here
        deleteButton.setBackgroundColor(Color.TRANSPARENT);  // Make the background transparent
        deleteButton.setPadding(16, 8, 16, 8);  // Optional: Adjust padding to fit the icon

        deleteButton.setOnClickListener(v -> {
            // Remove the row from the table
            tableLayout.removeView(tableRow);

            // Update the total calories and protein
            totalcalories -= calories;
            totalprotein -= protein;

            // Update the totalView TextView to reflect the new totals
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            textCalorie.setText("calories: " + String.valueOf(totalcalories));
            textDate.setText(date); // Assuming date is already a String
            textProtein.setText("protein: " + String.valueOf(totalprotein));
        });

        tableRow.addView(deleteButton);

        // Add the row to the table
        tableLayout.addView(tableRow);
    }


}
