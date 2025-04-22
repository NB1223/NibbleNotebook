package com.example.nibblenotebook.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.nibblenotebook.repository.RecipeRepository;
import java.util.List;
import java.util.Optional;

@Component
public class RecipeManager {
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    // Get all recipes
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }
    
    // Get recipe by ID
    public Optional<Recipe> getRecipeById(int id) {
        return recipeRepository.findById(id);
    }
    
    // Get recipes by user
    public List<Recipe> getRecipesByUser(User user) {
        return recipeRepository.findByUser(user);
    }
    
    // Save or update a recipe
    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }
    
    // Delete a recipe
    public void deleteRecipe(int recipeId) {
        recipeRepository.deleteById(recipeId);
    }
    
    // Search recipes by name
    public List<Recipe> searchRecipesByName(String name) {
        return recipeRepository.findByNameContainingIgnoreCase(name);
    }
    
    // Search recipes by cuisine
    public List<Recipe> searchRecipesByCuisine(String cuisine) {
        return recipeRepository.findByCuisineContainingIgnoreCase(cuisine);
    }
} 