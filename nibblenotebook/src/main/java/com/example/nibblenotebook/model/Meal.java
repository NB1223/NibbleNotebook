package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Meal")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    // @Enumerated(EnumType.STRING)
    @Column(name = "time", nullable = false)
    private String time;

    @ManyToMany
    @JoinTable(
      name = "Meal_Recipes", 
      joinColumns = @JoinColumn(name = "meal_id"), 
      inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> recipes;

    public Meal() {}

    public Meal(String name, String time) {
        this.name = name;
        this.time = time;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}

// // Enum for Meal Time
// enum MealTime {
//     BREAKFAST,
//     LUNCH,
//     DINNER,
//     SNACK;
// }
