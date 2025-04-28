package com.example.nibblenotebook.model;

import java.util.ArrayList;

import jakarta.persistence.*;

@Entity
@Table(name = "Recipe_Ingredients")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_ingredient_id")
    private int recipeingredientid;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Column(nullable = false)
    private double quantity;

    @ManyToOne
    @JoinColumn(name = "recipe_id") // or whatever your foreign key column is
    private Recipe recipe;


    public RecipeIngredient() {}

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, double quantity) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        if (recipe.getIngredients() == null) {
            recipe.setIngredients(new ArrayList<>());
        }
        recipe.getIngredients().add(this);
    }

    public int getRecipeIngredientId() { return recipeingredientid; }
    public Ingredient getIngredient() { return ingredient; }
    public double getQuantity() { return quantity; }
    public void setRecipeIngredient(int recipeingredientid) { this.recipeingredientid = recipeingredientid; }
    public void setIngredient(Ingredient ingredient) { this.ingredient = ingredient; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
}
