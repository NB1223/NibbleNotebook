package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.repository.RecipeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/search")
    public String searchRecipes(
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Boolean vegetarian,
            @RequestParam(required = false) Integer maxTime,
            HttpSession session,
            Model model) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        List<Recipe> recipes;
        
        // If no filters applied, get all recipes
        if ((cuisine == null || cuisine.isEmpty()) && vegetarian == null && maxTime == null) {
            recipes = recipeRepository.findAll();
        } else {
            // Start with all recipes
            recipes = new ArrayList<>(recipeRepository.findAll());
            
            // Apply filters
            if (cuisine != null && !cuisine.isEmpty()) {
                recipes.removeIf(recipe -> !recipe.getCuisine().equalsIgnoreCase(cuisine));
            }
            
            if (vegetarian != null && vegetarian) {
                recipes.removeIf(recipe -> !recipe.isVegetarian());
            }
            
            if (maxTime != null) {
                recipes.removeIf(recipe -> recipe.getTime() > maxTime);
            }
        }
        
        // Get a list of unique cuisines for the dropdown
        List<String> cuisines = recipeRepository.findDistinctCuisines();
        
        model.addAttribute("recipes", recipes);
        model.addAttribute("cuisines", cuisines);
        model.addAttribute("selectedCuisine", cuisine);
        model.addAttribute("vegetarian", vegetarian);
        model.addAttribute("maxTime", maxTime);
        model.addAttribute("name", session.getAttribute("name"));
        
        return "search_recipes";
    }
} 