package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Meal_Plan")
public class MealPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_plan_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"day\"", nullable = false)
    private DayOfWeek day;

    @ManyToMany
    @JoinTable(
        name = "Meal_Plan_Meals",
        joinColumns = @JoinColumn(name = "meal_plan_id"),
        inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private List<Meal> meals = new ArrayList<>();
    
    public enum DayOfWeek {
        Mon, Tues, Wed, Thurs, Fri, Sat, Sun
    }

    public MealPlan() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public DayOfWeek getDay() { return day; }
    public void setDay(DayOfWeek day) { this.day = day; }
    public List<Meal> getMeals() { return meals; }
    public void setMeals(List<Meal> meals) { this.meals = meals; }

    
    public void addMeal(Meal meal) {
        if (this.meals == null) {
            this.meals = new ArrayList<>();
        }
        this.meals.add(meal);
    }
    
    public void removeMeal(Meal meal) {
        if (this.meals != null) {
            this.meals.remove(meal);
        }
    }
    
    public List<Meal> getMealsForTime(Meal.MealTime mealTime) {
        List<Meal> filteredMeals = new ArrayList<>();
        
        if (mealTime == null || meals == null) {
            return filteredMeals;
        }
        
        for (Meal meal : meals) {
            if (meal != null && meal.getTime() == mealTime) {
                filteredMeals.add(meal);
            }
        }
        return filteredMeals;
    }
} 