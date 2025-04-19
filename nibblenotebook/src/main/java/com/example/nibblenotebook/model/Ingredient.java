package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Ingredients")
public class Ingredient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private int ingredientId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private double quantity;
    
    @Column(name = "measurement_unit", nullable = false)
    private String measurementUnit;
    
    @ManyToMany(mappedBy = "ingredients")
    private List<Recipe> recipes = new ArrayList<>();
    
    // Constructors
    public Ingredient() {}
    
    public Ingredient(String name, double quantity, String measurementUnit) {
        this.name = name;
        this.quantity = quantity;
        this.measurementUnit = measurementUnit;
    }
    
    // Getters and setters
    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public String getMeasurementUnit() { return measurementUnit; }
    public void setMeasurementUnit(String measurementUnit) { this.measurementUnit = measurementUnit; }
    public List<Recipe> getRecipes() { return recipes; }
    public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; }
}