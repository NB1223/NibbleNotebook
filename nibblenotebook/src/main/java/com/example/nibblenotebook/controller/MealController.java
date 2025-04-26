package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.*;
import com.example.nibblenotebook.repository.*;
import com.example.nibblenotebook.service.PantryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Controller
public class MealController {

    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private MealPlanRepository mealPlanRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PantryService pantryService;
    
    
    // View all meals page
    @GetMapping("/my-meals")
    public String viewMyMeals(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId);
        if (user == null) return "redirect:/login";
        
        List<Meal> meals = mealRepository.findByUser(user);
        
        model.addAttribute("meals", meals != null ? meals : new ArrayList<>());
        model.addAttribute("name", session.getAttribute("name"));
        
        return "my_meals";
    }
    
    // Add meal form page
    @GetMapping("/add-meal")
    public String showAddMealForm(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId);
        if (user == null) return "redirect:/login";
        
        List<Recipe> userRecipes = recipeRepository.findByUser(user);
        
        model.addAttribute("recipes", userRecipes != null ? userRecipes : new ArrayList<>());
        model.addAttribute("mealTimes", Meal.MealTime.values());
        model.addAttribute("name", session.getAttribute("name"));
        
        return "add_meal";
    }
    
    // Process add meal form
    @PostMapping("/add-meal")
    public String addMeal(@RequestParam String mealName,
                         @RequestParam Meal.MealTime mealTime,
                         @RequestParam(required = false) List<Integer> recipeIds,
                         HttpSession session) {
                         
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId);
        if (user == null) return "redirect:/login";
        
        Meal meal = Meal.builder()
                .name(mealName)
                .time(mealTime)
                .user(user)
                .build();
        
        if (recipeIds != null && !recipeIds.isEmpty()) {
            for (Integer recipeId : recipeIds) {
                Recipe recipe = recipeRepository.findById(recipeId);
                if (recipe != null) {
                    meal.addRecipe(recipe);
                }
            }
        }
        
        mealRepository.save(meal);
        
        return "redirect:/my-meals";
    }
    
    // View meal plan page
    @GetMapping("/meal-plan")
    public String viewMealPlan(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId);
        if (user == null) return "redirect:/login";
        
        try {
            System.out.println("Fetching meal plans for user ID: " + userId);
            
            List<MealPlan> mealPlans = mealPlanRepository.findByUser(user);
            System.out.println("Found " + (mealPlans != null ? mealPlans.size() : 0) + " meal plans");
            
            List<Meal> userMeals = mealRepository.findByUser(user);
            System.out.println("Found " + (userMeals != null ? userMeals.size() : 0) + " meals for user");
            
            // Organize meal plans by day
            if (mealPlans != null && !mealPlans.isEmpty()) {
                mealPlans.sort((mp1, mp2) -> mp1.getDay().ordinal() - mp2.getDay().ordinal());
            }
            
            model.addAttribute("mealPlans", mealPlans != null ? mealPlans : new ArrayList<>());
            model.addAttribute("meals", userMeals != null ? userMeals : new ArrayList<>());
            model.addAttribute("days", MealPlan.DayOfWeek.values());
            model.addAttribute("mealTimes", Meal.MealTime.values());
            model.addAttribute("name", session.getAttribute("name"));
            
            return "meal_plan";
        } catch (Exception e) {
            System.err.println("Error loading meal plan: " + e.getMessage());
            e.printStackTrace();
            
            // Add default empty lists to prevent template errors
            model.addAttribute("mealPlans", new ArrayList<>());
            model.addAttribute("meals", new ArrayList<>());
            model.addAttribute("days", MealPlan.DayOfWeek.values());
            model.addAttribute("mealTimes", Meal.MealTime.values());
            model.addAttribute("name", session.getAttribute("name"));
            model.addAttribute("errorMessage", "Error loading meal plan: " + e.getMessage());
            
            return "meal_plan";
        }
    }
    
    // Process add to meal plan form
    @PostMapping("/add-to-meal-plan")
    public String addToMealPlan(@RequestParam MealPlan.DayOfWeek day,
                              @RequestParam(name = "mealTime") Meal.MealTime mealTime,
                              @RequestParam(name = "mealIds", required = false) List<Integer> mealIds,
                              HttpSession session, Model model) {
                              
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId);
        if (user == null) return "redirect:/login";
        
        try {
            System.out.println("Adding to meal plan for user ID: " + userId);
            System.out.println("Day: " + day + ", Meal Time: " + mealTime);
            System.out.println("Selected meal IDs: " + (mealIds != null ? mealIds : "none"));
            
            // Check if meal plan for this day already exists
            List<MealPlan> existingPlans = mealPlanRepository.findByUserAndDay(user, day);
            MealPlan mealPlan;
            
            if (existingPlans != null && !existingPlans.isEmpty()) {
                mealPlan = existingPlans.get(0);
                System.out.println("Found existing meal plan ID: " + mealPlan.getId());
                
                // Don't clear existing meals, just manage the ones for this meal time
                List<Meal> currentMeals = new ArrayList<>();
                if (mealPlan.getMeals() != null) {
                    currentMeals.addAll(mealPlan.getMeals());
                }
                
                // Remove meals for this specific meal time
                currentMeals.removeIf(meal -> meal != null && meal.getTime() == mealTime);
                System.out.println("Removed existing meals for meal time: " + mealTime);
                
                // Keep the filtered meals (other meal times)
                mealPlan.setMeals(currentMeals);
            } else {
                mealPlan = new MealPlan();
                mealPlan.setUser(user);
                mealPlan.setDay(day);
                mealPlan.setMeals(new ArrayList<>());
                System.out.println("Created new meal plan for day: " + day);
            }
            
            // Add selected meals for this meal time
            int addedCount = 0;
            if (mealIds != null && !mealIds.isEmpty()) {
                for (Integer mealId : mealIds) {
                    mealRepository.findById(mealId).ifPresent(meal -> {
                        if (meal.getUser().getId() == userId) {
                            mealPlan.addMeal(meal);
                            System.out.println("Added meal ID: " + meal.getId() + ", name: " + meal.getName());
                        }
                    });
                    addedCount++;
                }
                System.out.println("Added " + addedCount + " meals to the plan");
            }
            
            mealPlanRepository.save(mealPlan);
            System.out.println("Saved meal plan successfully");
            
            return "redirect:/meal-plan";
        } catch (Exception e) {
            System.err.println("Error adding to meal plan: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("errorMessage", "Error adding to meal plan: " + e.getMessage());
            return viewMealPlan(session, model); // Re-render the meal plan page with error
        }
    }
    
    // View meal details page
    @GetMapping("/meals/{mealId}/view")
    public String viewMealDetails(@PathVariable int mealId,
                                HttpSession session,
                                Model model) {
                                
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        Meal meal = mealRepository.findById(mealId).orElse(null);
        if (meal == null) return "redirect:/my-meals";
        
        model.addAttribute("meal", meal);
        model.addAttribute("name", session.getAttribute("name"));
        
        return "meal_view";
    }
    
    // Delete meal
    @PostMapping("/meals/{mealId}/delete")
    public String deleteMeal(@PathVariable int mealId,
                           HttpSession session) {
                           
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId);
        Meal meal = mealRepository.findById(mealId).orElse(null);
        
        if (meal != null && meal.getUser().getId() == userId) {
            mealRepository.deleteById(mealId);
        }
        
        return "redirect:/my-meals";
    }
    
    // Shopping list page
    @GetMapping("/shopping-list")
    public String viewShoppingList(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId);
        if (user == null) return "redirect:/login";
        
        List<ShoppingListItem> shoppingList = new ArrayList<>();
        
        try {
            System.out.println("Starting shopping list generation for user ID: " + userId);
            
            // Create manual shopping list instead of navigating through relationships
            // Step 1: Get all meal plans for this user
            List<MealPlan> userMealPlans = mealPlanRepository.findByUser(user);
            System.out.println("Found " + (userMealPlans != null ? userMealPlans.size() : 0) + " meal plans");
            
            if (userMealPlans != null && !userMealPlans.isEmpty()) {
                // Step 2: Get all ingredients across all recipes in meals in these meal plans
                Map<Integer, ShoppingListItem> ingredientMap = new HashMap<>();
                
                for (MealPlan mealPlan : userMealPlans) {
                    System.out.println("Processing meal plan ID: " + mealPlan.getId() + " for day: " + mealPlan.getDay());
                    
                    // Get all meals for this meal plan
                    List<Meal> meals = mealPlan.getMeals();
                    System.out.println("  Found " + (meals != null ? meals.size() : 0) + " meals in this plan");
                    
                    if (meals != null) {
                        for (Meal meal : meals) {
                            if (meal != null) {
                                System.out.println("    Processing meal ID: " + meal.getId() + ", name: " + meal.getName());
                                List<Recipe> recipes = meal.getRecipes();
                                System.out.println("      Found " + (recipes != null ? recipes.size() : 0) + " recipes in this meal");
                                
                                // Each meal has recipes
                                if (recipes != null) {
                                    for (Recipe recipe : recipes) {
                                        if (recipe != null) {
                                            System.out.println("        Processing recipe ID: " + recipe.getId() + ", name: " + recipe.getName());
                                            
                                            // Manually fetch recipe ingredients 
                                            List<RecipeIngredient> ingredients = recipeRepository.findIngredientsForRecipe(recipe.getId());
                                            System.out.println("          Found " + (ingredients != null ? ingredients.size() : 0) + " ingredients");
                                            
                                            if (ingredients != null) {
                                                for (RecipeIngredient ri : ingredients) {
                                                    if (ri != null && ri.getIngredient() != null) {
                                                        int ingredientId = ri.getIngredient().getId();
                                                        double quantity = ri.getQuantity();
                                                        
                                                        System.out.println("            Adding ingredient: " + ri.getIngredient().getName() + 
                                                                          ", quantity: " + quantity);
                                                        
                                                        // If already in map, add quantity
                                                        if (ingredientMap.containsKey(ingredientId)) {
                                                            ShoppingListItem existingItem = ingredientMap.get(ingredientId);
                                                            existingItem.setQuantity(existingItem.getQuantity() + quantity);
                                                            System.out.println("              Updated quantity to: " + existingItem.getQuantity());
                                                        } else {
                                                            // Otherwise create new
                                                            ShoppingListItem newItem = new ShoppingListItem(ri.getIngredient(), quantity);
                                                            ingredientMap.put(ingredientId, newItem);
                                                            System.out.println("              Added new item");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                
                System.out.println("Finished collecting required ingredients. Total items: " + ingredientMap.size());
                
                // Step 3: Subtract ingredients already in pantry
                List<UserIngredient> pantryItems = pantryService.getUserPantry(user);
                System.out.println("Found " + (pantryItems != null ? pantryItems.size() : 0) + " pantry items");
                
                if (pantryItems != null) {
                    for (UserIngredient ui : pantryItems) {
                        if (ui != null && ui.getIngredient() != null) {
                            int ingredientId = ui.getIngredient().getId();
                            System.out.println("  Checking pantry item: " + ui.getIngredient().getName() + ", quantity: " + ui.getQuantity());
                            
                            if (ingredientMap.containsKey(ingredientId)) {
                                ShoppingListItem item = ingredientMap.get(ingredientId);
                                double newQuantity = item.getQuantity() - ui.getQuantity();
                                System.out.println("    Found in shopping list. Adjusting from " + item.getQuantity() + " to " + newQuantity);
                                
                                if (newQuantity <= 0) {
                                    // Remove if we have enough or more
                                    ingredientMap.remove(ingredientId);
                                    System.out.println("    Removed from shopping list (sufficient quantity in pantry)");
                                } else {
                                    // Update quantity if we need more
                                    item.setQuantity(newQuantity);
                                    System.out.println("    Updated quantity to: " + newQuantity);
                                }
                            }
                        }
                    }
                }
                
                // Convert map to list
                shoppingList = new ArrayList<>(ingredientMap.values());
                System.out.println("Final shopping list contains " + shoppingList.size() + " items");
            }
            
        } catch (Exception e) {
            System.err.println("Error generating shopping list: " + e.getMessage());
            e.printStackTrace();
            // Add the error message to the model for debugging
            model.addAttribute("errorMessage", "Error: " + e.getMessage());
        }
        
        model.addAttribute("shoppingList", shoppingList);
        model.addAttribute("name", session.getAttribute("name"));
        
        return "shopping_list";
    }
} 