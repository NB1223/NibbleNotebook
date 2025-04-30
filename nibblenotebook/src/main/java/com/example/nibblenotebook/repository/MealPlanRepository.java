package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.MealPlan;
import com.example.nibblenotebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealPlanRepository extends JpaRepository<MealPlan, Integer> {
    List<MealPlan> findByUser(User user);
    List<MealPlan> findByUserAndDay(User user, MealPlan.DayOfWeek day);
} 