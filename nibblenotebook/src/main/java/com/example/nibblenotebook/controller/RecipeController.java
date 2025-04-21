package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.Ingredient;
import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.RecipeIngredient;
import com.example.nibblenotebook.model.RecipeStep;
import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.repository.IngredientRepository;
import com.example.nibblenotebook.repository.RecipeIngredientRepository;
import com.example.nibblenotebook.repository.RecipeRepository;
import com.example.nibblenotebook.repository.RecipeStepRepository;
import com.example.nibblenotebook.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

@Controller
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private IngredientRepository ingredientRepo;

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepo;

    @Autowired
    private RecipeStepRepository recipeStepRepo;

    @GetMapping("/my-recipes")
    public String viewMyRecipes(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepo.findById(userId).orElse(null);
        List<Recipe> recipes = recipeRepo.findByUser(user);

        model.addAttribute("recipes", recipes);
        model.addAttribute("name", session.getAttribute("name"));

        return "my_recipes";
    }

    @GetMapping("/add-recipe")
    public String showAddRecipeForm(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        model.addAttribute("recipe", Recipe.builder().build());
        model.addAttribute("name", session.getAttribute("name"));

        return "add_recipe";
    }

    @PostMapping("/add-recipe")
    public String addRecipe(@ModelAttribute("recipe") Recipe recipe, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepo.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";

        Recipe newRecipe = Recipe.builder()
                .user(user)
                .name(recipe.getName())
                .description(recipe.getDescription())
                .cuisine(recipe.getCuisine())
                .time(recipe.getTime())
                .build();

        recipeRepo.save(newRecipe);

        return "redirect:/recipes/my-recipes";
    }

    @GetMapping("/{recipeId}/ingredients")
    public String viewRecipeIngredients(
            @PathVariable("recipeId") int recipeId,
            HttpSession session,
            Model model) {
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if (recipe == null || recipe.getUser().getId() != userId) {
            return "redirect:/recipes/my-recipes";
        }

        // Initialize ingredients if null
        if (recipe.getIngredients() == null) {
            recipe.setIngredients(new ArrayList<>());
        }

        model.addAttribute("recipe", recipe);
        model.addAttribute("allIngredients", ingredientRepo.findAll());
        model.addAttribute("name", session.getAttribute("name"));

        return "recipe_ingredients"; // Make sure this file exists!
    }

    @PostMapping("/{recipeId}/ingredients/add")
    public String addIngredientToRecipe(@PathVariable int recipeId,
                                        @RequestParam int ingredientId,
                                        @RequestParam double quantity) {
        Recipe recipe = recipeRepo.findById(recipeId).orElseThrow();
        Ingredient ingredient = ingredientRepo.findById(ingredientId).orElseThrow();
    
        // Check if relationship already exists
        RecipeIngredient existing = recipe.getIngredients().stream()
            .filter(ri -> ri.getIngredient().getId() == ingredientId)
            .findFirst()
            .orElse(null);
    
        if (existing != null) {
            existing.setQuantity(quantity);
        } else {
            RecipeIngredient newRi = new RecipeIngredient();
            newRi.setRecipe(recipe);
            newRi.setIngredient(ingredient);
            newRi.setQuantity(quantity);
            recipe.getIngredients().add(newRi);
            recipeIngredientRepo.save(newRi);
        }
        
        return "redirect:/recipes/" + recipeId + "/ingredients";
    }

    @PostMapping("/ingredients/delete")
    public String deleteIngredientFromRecipe(@RequestParam int recipeIngredientId,
                                        @RequestParam int recipeId,
                                        HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        // Verify the recipe belongs to the user
        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if (recipe == null || recipe.getUser().getId() != userId) {
            return "redirect:/recipes/my-recipes";
        }
        
        recipeIngredientRepo.deleteById(recipeIngredientId);
        return "redirect:/recipes/" + recipeId + "/ingredients";
    }

    @GetMapping("/{recipeId}/steps")
    public String viewRecipeSteps(@PathVariable("recipeId") int recipeId,
                                HttpSession session,
                                Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if (recipe == null || recipe.getUser().getId() != userId) {
            return "redirect:/recipes/my-recipes";
        }

        List<RecipeStep> steps = recipeStepRepo.findByRecipeId(recipeId);
        steps.sort(Comparator.comparingInt(RecipeStep::getStepNumber));

        model.addAttribute("recipe", recipe);
        model.addAttribute("steps", steps);
        model.addAttribute("name", session.getAttribute("name"));

        return "recipe_steps";
    }

    @PostMapping("/{recipeId}/steps/add")
    public String addStepToRecipe(@PathVariable int recipeId,
                                @RequestParam String instruction,
                                HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if (recipe == null || recipe.getUser().getId() != userId) {
            return "redirect:/recipes/my-recipes";
        }

        // Get current steps to determine next step number
        List<RecipeStep> currentSteps = recipeStepRepo.findByRecipeId(recipeId);
        int nextStepNumber = currentSteps.isEmpty() ? 1 : 
                            currentSteps.stream()
                                .mapToInt(RecipeStep::getStepNumber)
                                .max().getAsInt() + 1;

        RecipeStep newStep = new RecipeStep(recipe, nextStepNumber, instruction);
        recipeStepRepo.save(newStep);

        return "redirect:/recipes/" + recipeId + "/steps";
    }

    @PostMapping("/steps/delete")
    public String deleteStepFromRecipe(@RequestParam int stepId,
                                    @RequestParam int recipeId,
                                    HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Recipe recipe = recipeRepo.findById(recipeId).orElse(null);
        if (recipe == null || recipe.getUser().getId() != userId) {
            return "redirect:/recipes/my-recipes";
        }

        recipeStepRepo.deleteById(stepId);
        
        // Re-number remaining steps
        List<RecipeStep> remainingSteps = recipeStepRepo.findByRecipeId(recipeId);
        remainingSteps.sort(Comparator.comparingInt(RecipeStep::getStepNumber));
        
        for (int i = 0; i < remainingSteps.size(); i++) {
            RecipeStep step = remainingSteps.get(i);
            step.setStepNumber(i + 1);
            recipeStepRepo.save(step);
        }

        return "redirect:/recipes/" + recipeId + "/steps";
    }
}
