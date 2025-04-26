package com.example.nibblenotebook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Recipe_Ingredients")
public class UserIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingredient_id")
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    
    @Column(nullable = false)
    private double quantity;
    
    // Constructors, getters, and setters
    public UserIngredient() {}

    public UserIngredient(Recipe recipe, Ingredient ingredient, double quantity) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }
    
    // For compatibility with previous code
    public UserIngredient(User user, Ingredient ingredient, double quantity) {
        // Create a temporary recipe for user's ingredient storage
        Recipe tempRecipe = new Recipe();
        tempRecipe.setUser(user);
        tempRecipe.setName("User Ingredient: " + ingredient.getName());
        
        this.recipe = tempRecipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    // Getters and setters
    public int getId() {
        return id;
    }
    
    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
    
    public User getUser() {
        return recipe != null ? recipe.getUser() : null;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}