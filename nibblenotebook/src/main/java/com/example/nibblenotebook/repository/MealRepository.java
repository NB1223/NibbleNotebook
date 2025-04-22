package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Meal;
import com.example.nibblenotebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<Meal, Integer> {
    List<Meal> findByUser(User user);
    
    @Query("SELECT m FROM Meal m JOIN m.recipes r WHERE r.id = :recipeId")
    List<Meal> findByRecipeId(int recipeId);
    
    List<Meal> findByNameContainingIgnoreCase(String name);
    
    List<Meal> findByUserAndTime(User user, Meal.MealTime time);
} 