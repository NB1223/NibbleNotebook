package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "User") // Updated to match actual table name in database
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
    private List<MealPlan> mealPlans;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Meal> meals;
    
    public User() {}

    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

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
    
    public List<ShoppingListItem> generateShoppingList() {
        List<ShoppingListItem> shoppingList = new ArrayList<>();
        
        try {
            if (mealPlans != null && !mealPlans.isEmpty()) {
                for (MealPlan mealPlan : mealPlans) {
                    if (mealPlan != null && mealPlan.getMeals() != null && !mealPlan.getMeals().isEmpty()) {
                        for (Meal meal : mealPlan.getMeals()) {
                            if (meal != null && meal.getRecipes() != null && !meal.getRecipes().isEmpty()) {
                                for (Recipe recipe : meal.getRecipes()) {
                                    if (recipe != null && recipe.getIngredients() != null && !recipe.getIngredients().isEmpty()) {
                                        for (RecipeIngredient ri : recipe.getIngredients()) {
                                            if (ri != null && ri.getIngredient() != null) {
                                                addToShoppingList(shoppingList, ri.getIngredient(), ri.getQuantity());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            

        } catch (Exception e) {
            System.err.println("Error in generateShoppingList: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        
        return shoppingList;
    }
    
    private void addToShoppingList(List<ShoppingListItem> list, Ingredient ingredient, double quantity) {
        if (list == null || ingredient == null) {
            return;
        }
        
        for (ShoppingListItem item : list) {
            if (item.getIngredient().getId() == ingredient.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        
        list.add(new ShoppingListItem(ingredient, quantity));
    }
    
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
