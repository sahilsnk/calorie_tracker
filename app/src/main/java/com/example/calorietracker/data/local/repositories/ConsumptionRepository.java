package com.example.calorietracker.data.local.repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.calorietracker.data.local.db.FoodDatabase;
import com.example.calorietracker.data.local.db.dao.ConsumptionDao;
import com.example.calorietracker.data.local.entities.Consumption;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConsumptionRepository {

    private ConsumptionDao consumptionDao;
    private String todayDate;

    public ConsumptionRepository(Application application) {
        FoodDatabase db = FoodDatabase.getDatabase(application);
        consumptionDao = db.consumptionDao();
        todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }
    public LiveData<Consumption> getConsumptionForToday() {
        return new LiveData<Consumption>() {
            @Override
            protected void onActive() {
                super.onActive();
                new Thread(() -> postValue(consumptionDao.getConsumptionByDate(todayDate))).start();
            }
        };
    }

    public void insertOrUpdate(float calories, float protein) {
        new Thread(() -> {
            Consumption consumption = consumptionDao.getConsumptionByDate(todayDate);
            if (consumption == null) {
                consumption = new Consumption(todayDate, calories, protein);
                consumptionDao.insert(consumption);
            } else {
                consumptionDao.updateConsumption(todayDate, consumption.getTotalCalories() + calories, consumption.getTotalProtein() + protein);
            }
        }).start();
    }

    public void resetDailyConsumption() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        new Thread(() -> consumptionDao.deleteConsumptionForDate(todayDate)).start();
    }

    public void deleteAll() {
        new Thread(() -> consumptionDao.deleteAll()).start();
    }
}
