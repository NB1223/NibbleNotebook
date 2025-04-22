package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Meal;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Modifying;
// import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Integer> {
    List<Meal> findByTime(String time); // Fetch meals based on the time (Breakfast, Lunch, etc.)

    // @Modifying
    // @Query("UPDATE Meal m SET m.userId = :userId WHERE m.id = :mealId")
    // // You can add more custom queries if needed for saving meals to users
    // void saveMealToUser(int userId, int mealId); // Custom method to save meal for a user
}
