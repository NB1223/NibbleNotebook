package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Recipe")
public class Recipe {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private int recipeId;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String cuisine;
    
    private Double time;
    
    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> steps = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "Recipe_Ingredients",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    private List<Ingredient> ingredients = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "Recipe_Tags",
        joinColumns = @JoinColumn(name = "recipe_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_name")
    )
    private List<Tag> tags = new ArrayList<>();
    
    // Constructors
    public Recipe() {}
    
    public Recipe(String name, String description, String cuisine, Double time) {
        this.name = name;
        this.description = description;
        this.cuisine = cuisine;
        this.time = time;
    }
    
    // Getters and setters
    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    public Double getTime() { return time; }
    public void setTime(Double time) { this.time = time; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public List<RecipeStep> getSteps() { return steps; }
    public void setSteps(List<RecipeStep> steps) { this.steps = steps; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }
    
    // Helper methods
    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        ingredient.getRecipes().add(this);
    }
    
    public void removeIngredient(Ingredient ingredient) {
        ingredients.remove(ingredient);
        ingredient.getRecipes().remove(this);
    }
    
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getRecipes().add(this);
    }
    
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getRecipes().remove(this);
    }
    
    public void addStep(RecipeStep step) {
        steps.add(step);
        step.setRecipe(this);
    }
}