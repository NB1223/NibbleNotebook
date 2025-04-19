package com.example.nibblenotebook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Recipe_Steps")
public class RecipeStep {
    
    @EmbeddedId
    private RecipeStepId id;
    
    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
    
    @Column(nullable = false)
    private String instruction;
    
    // Constructors
    public RecipeStep() {}
    
    public RecipeStep(Recipe recipe, int stepNumber, String instruction) {
        this.id = new RecipeStepId(recipe.getRecipeId(), stepNumber);
        this.recipe = recipe;
        this.instruction = instruction;
    }
    
    // Getters and setters
    public RecipeStepId getId() { return id; }
    public void setId(RecipeStepId id) { this.id = id; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
    public int getStepNumber() { return id.getStepNumber(); }
}

@Embeddable
class RecipeStepId implements java.io.Serializable {
    
    private int recipeId;
    private int stepNumber;
    
    public RecipeStepId() {}
    
    public RecipeStepId(int recipeId, int stepNumber) {
        this.recipeId = recipeId;
        this.stepNumber = stepNumber;
    }
    
    // Getters and setters
    public int getRecipeId() { return recipeId; }
    public void setRecipeId(int recipeId) { this.recipeId = recipeId; }
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeStepId that = (RecipeStepId) o;
        return recipeId == that.recipeId && stepNumber == that.stepNumber;
    }
    
    @Override
    public int hashCode() {
        return 31 * recipeId + stepNumber;
    }
}