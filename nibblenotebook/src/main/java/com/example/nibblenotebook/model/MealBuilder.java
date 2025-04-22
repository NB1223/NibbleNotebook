package com.example.nibblenotebook.model;

import java.util.ArrayList;
import java.util.List;

public class MealBuilder {
    private int id;
    private String name;
    private Meal.MealTime time;
    private List<Recipe> recipes = new ArrayList<>();
    private User user;

    public MealBuilder() {}

    public MealBuilder id(int id) {
        this.id = id;
        return this;
    }

    public MealBuilder name(String name) {
        this.name = name;
        return this;
    }

    public MealBuilder time(Meal.MealTime time) {
        this.time = time;
        return this;
    }

    public MealBuilder recipes(List<Recipe> recipes) {
        this.recipes = recipes;
        return this;
    }

    public MealBuilder addRecipe(Recipe recipe) {
        this.recipes.add(recipe);
        return this;
    }

    public MealBuilder user(User user) {
        this.user = user;
        return this;
    }

    public Meal build() {
        Meal meal = new Meal();
        meal.setId(this.id);
        meal.setName(this.name);
        meal.setTime(this.time);
        meal.setRecipes(this.recipes);
        meal.setUser(this.user);
        return meal;
    }
} 