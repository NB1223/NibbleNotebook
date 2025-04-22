package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users") // Change from "User" to "users" to avoid SQL reserved keyword
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;

    @Column(name = "user_name", nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name; // Full name

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Recipe> recipes;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserIngredient> userIngredients;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MealPlan> mealPlans;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Meal> meals;
    
    @ManyToMany
    @JoinTable(
        name = "User_Saved_Recipes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> savedRecipes;
    
    @ManyToMany
    @JoinTable(
        name = "User_Saved_Meals",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "meal_id")
    )
    private List<Meal> savedMeals;

    // Constructors
    public User() {}

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }
    
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
    
    public List<UserIngredient> getUserIngredients() {
        return userIngredients;
    }
    
    public void setUserIngredients(List<UserIngredient> userIngredients) {
        this.userIngredients = userIngredients;
    }
    
    public List<MealPlan> getMealPlans() {
        return mealPlans;
    }
    
    public void setMealPlans(List<MealPlan> mealPlans) {
        this.mealPlans = mealPlans;
    }
    
    public List<Meal> getMeals() {
        return meals;
    }
    
    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }
    
    public List<Recipe> getSavedRecipes() {
        return savedRecipes;
    }
    
    public void setSavedRecipes(List<Recipe> savedRecipes) {
        this.savedRecipes = savedRecipes;
    }
    
    public List<Meal> getSavedMeals() {
        return savedMeals;
    }
    
    public void setSavedMeals(List<Meal> savedMeals) {
        this.savedMeals = savedMeals;
    }
    
    // Shopping list generation
    public List<ShoppingListItem> generateShoppingList() {
        List<ShoppingListItem> shoppingList = new ArrayList<>();
        
        // Calculate required ingredients from meal plans
        if (mealPlans != null) {
            for (MealPlan mealPlan : mealPlans) {
                for (Meal meal : mealPlan.getMeals()) {
                    for (Recipe recipe : meal.getRecipes()) {
                        for (RecipeIngredient ri : recipe.getIngredients()) {
                            addToShoppingList(shoppingList, ri.getIngredient(), ri.getQuantity());
                        }
                    }
                }
            }
        }
        
        // Subtract ingredients already in pantry
        if (userIngredients != null) {
            for (UserIngredient ui : userIngredients) {
                subtractFromShoppingList(shoppingList, ui.getIngredient(), ui.getQuantity());
            }
        }
        
        return shoppingList;
    }
    
    private void addToShoppingList(List<ShoppingListItem> list, Ingredient ingredient, double quantity) {
        // Find if ingredient already exists in list
        for (ShoppingListItem item : list) {
            if (item.getIngredient().getId() == ingredient.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        
        // If not found, add new
        list.add(new ShoppingListItem(ingredient, quantity));
    }
    
    private void subtractFromShoppingList(List<ShoppingListItem> list, Ingredient ingredient, double quantity) {
        for (ShoppingListItem item : list) {
            if (item.getIngredient().getId() == ingredient.getId()) {
                double newQuantity = item.getQuantity() - quantity;
                if (newQuantity <= 0) {
                    list.remove(item);
                } else {
                    item.setQuantity(newQuantity);
                }
                return;
            }
        }
    }
    
    // Inner class for shopping list items
    public static class ShoppingListItem {
        private Ingredient ingredient;
        private double quantity;
        
        public ShoppingListItem(Ingredient ingredient, double quantity) {
            this.ingredient = ingredient;
            this.quantity = quantity;
        }
        
        public Ingredient getIngredient() {
            return ingredient;
        }
        
        public double getQuantity() {
            return quantity;
        }
        
        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }
    }
}
