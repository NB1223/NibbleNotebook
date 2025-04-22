package com.example.nibblenotebook.model;

import java.util.ArrayList;
import java.util.List;

public class RecipeBuilder {
    private int id;
    private User user;
    private String name;
    private String description;
    private String cuisine;
    private double time;
    private boolean vegetarian;
    private List<RecipeStep> steps = new ArrayList<>();
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public RecipeBuilder() {}

    public RecipeBuilder id(int id) {
        this.id = id;
        return this;
    }

    public RecipeBuilder user(User user) {
        this.user = user;
        return this;
    }

    public RecipeBuilder name(String name) {
        this.name = name;
        return this;
    }

    public RecipeBuilder description(String description) {
        this.description = description;
        return this;
    }

    public RecipeBuilder cuisine(String cuisine) {
        this.cuisine = cuisine;
        return this;
    }

    public RecipeBuilder time(double time) {
        this.time = time;
        return this;
    }
    
    public RecipeBuilder vegetarian(boolean vegetarian) {
        this.vegetarian = vegetarian;
        return this;
    }

    public RecipeBuilder steps(List<RecipeStep> steps) {
        this.steps = steps;
        return this;
    }

    public RecipeBuilder addStep(RecipeStep step) {
        this.steps.add(step);
        return this;
    }

    public RecipeBuilder ingredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public RecipeBuilder addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public Recipe build() {
        Recipe recipe = new Recipe();
        recipe.setId(this.id);
        recipe.setUser(this.user);
        recipe.setName(this.name);
        recipe.setDescription(this.description);
        recipe.setCuisine(this.cuisine);
        recipe.setTime(this.time);
        recipe.setVegetarian(this.vegetarian);
        recipe.setSteps(this.steps);
        recipe.setIngredients(this.ingredients);
        return recipe;
    }
}