package com.example.nibblenotebook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Recipe_Steps")
public class RecipeStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_step_id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @Column(name = "step_number")
    private int stepNumber;

    private String instruction;

    public RecipeStep() {}

    public RecipeStep(Recipe recipe, int stepNumber, String instruction) {
        this.recipe = recipe;
        this.stepNumber = stepNumber;
        this.instruction = instruction;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Recipe getRecipe() { return recipe; }
    public void setRecipe(Recipe recipe) { this.recipe = recipe; }
    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }
}
