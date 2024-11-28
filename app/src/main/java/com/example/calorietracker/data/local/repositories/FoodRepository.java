// FoodRepository.java
package com.example.calorietracker.data.local.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.calorietracker.data.local.db.FoodDatabase;
import com.example.calorietracker.data.local.db.dao.FoodDao;
import com.example.calorietracker.data.local.entities.Food;

import java.util.List;

public class FoodRepository {

    private FoodDao foodDao;
    private LiveData<List<Food>> allFoods;

    public FoodRepository(Application application) {
        FoodDatabase db = FoodDatabase.getDatabase(application);
        foodDao = db.foodDao();
        allFoods = foodDao.getAllFoods();
    }

    public LiveData<List<Food>> getAllFoods() {
        return allFoods;
    }

    public void insert(Food food) {
        new Thread(() -> foodDao.insert(food)).start();
    }

    public LiveData<Food> getFoodByName(String name) {
        return foodDao.getFoodByName(name);
    }

    public void deleteAll() {
        new Thread(() -> foodDao.deleteAll()).start();
    }
}
