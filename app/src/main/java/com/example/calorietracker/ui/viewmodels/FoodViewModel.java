// FoodViewModel.java
package com.example.calorietracker.ui.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.calorietracker.data.local.entities.Consumption;
import com.example.calorietracker.data.local.repositories.ConsumptionRepository;
import com.example.calorietracker.data.local.repositories.FoodRepository;
import com.example.calorietracker.data.local.entities.Food;

import java.util.List;
public class FoodViewModel extends AndroidViewModel {


    private FoodRepository foodRepository;
    private ConsumptionRepository consumptionRepository; // Add this line
    private LiveData<List<Food>> allFoods;

    public FoodViewModel(Application application) {
        super(application);
        foodRepository = new FoodRepository(application);
        consumptionRepository = new ConsumptionRepository(application); // Add this line
        allFoods = foodRepository.getAllFoods();
    }

    public LiveData<List<Food>> getAllFoods() {
        return allFoods;
    }

    public void insert(Food food) {
        foodRepository.insert(food);
    }

    public LiveData<Food> getFoodByName(String name) {
        return foodRepository.getFoodByName(name);
    }
    public LiveData<Consumption> getConsumptionForToday() {
        return consumptionRepository.getConsumptionForToday();
    }
    public void deleteAll() {
        foodRepository.deleteAll();
    }

    public void insertOrUpdateConsumption(float calories, float protein) {
        consumptionRepository.insertOrUpdate(calories, protein);
    }

    public void resetDailyConsumption() {
        consumptionRepository.resetDailyConsumption();
    }

    public void deleteAllConsumption() {
        consumptionRepository.deleteAll();
    }
}
