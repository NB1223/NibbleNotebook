package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.*;
import com.example.nibblenotebook.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    private RecipeManager recipeManager;
    
    @Autowired
    private UserManager userManager;
    
    // View all meals page
    @GetMapping("/my-meals")
    public String viewMyMeals(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        
        List<Meal> meals = mealRepository.findByUser(user);
        
        model.addAttribute("meals", meals);
        model.addAttribute("name", session.getAttribute("name"));
        
        return "my_meals";
    }
    
    // Add meal form page
    @GetMapping("/add-meal")
    public String showAddMealForm(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        
        List<Recipe> userRecipes = recipeRepository.findByUser(user);
        
        model.addAttribute("recipes", userRecipes);
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
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        
        Meal meal = Meal.builder()
                .name(mealName)
                .time(mealTime)
                .user(user)
                .build();
        
        if (recipeIds != null) {
            for (Integer recipeId : recipeIds) {
                recipeRepository.findById(recipeId).ifPresent(meal::addRecipe);
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
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        
        List<MealPlan> mealPlans = mealPlanRepository.findByUser(user);
        List<Meal> userMeals = mealRepository.findByUser(user);
        
        // Organize meal plans by day
        mealPlans.sort((mp1, mp2) -> mp1.getDay().ordinal() - mp2.getDay().ordinal());
        
        model.addAttribute("mealPlans", mealPlans);
        model.addAttribute("meals", userMeals);
        model.addAttribute("days", MealPlan.DayOfWeek.values());
        model.addAttribute("mealTimes", Meal.MealTime.values());
        model.addAttribute("name", session.getAttribute("name"));
        
        return "meal_plan";
    }
    
    // Process add to meal plan form
    @PostMapping("/add-to-meal-plan")
    public String addToMealPlan(@RequestParam MealPlan.DayOfWeek day,
                              @RequestParam(required = false) List<Integer> mealIds,
                              HttpSession session) {
                              
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        
        // Check if meal plan for this day already exists
        List<MealPlan> existingPlans = mealPlanRepository.findByUserAndDay(user, day);
        MealPlan mealPlan;
        
        if (!existingPlans.isEmpty()) {
            mealPlan = existingPlans.get(0);
            // Clear existing meals
            mealPlan.setMeals(new ArrayList<>());
        } else {
            mealPlan = new MealPlan();
            mealPlan.setUser(user);
            mealPlan.setDay(day);
        }
        
        if (mealIds != null) {
            for (Integer mealId : mealIds) {
                mealRepository.findById(mealId).ifPresent(mealPlan::addMeal);
            }
        }
        
        mealPlanRepository.save(mealPlan);
        
        return "redirect:/meal-plan";
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
        
        User user = userRepository.findById(userId).orElse(null);
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
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return "redirect:/login";
        
        List<User.ShoppingListItem> shoppingList = user.generateShoppingList();
        
        model.addAttribute("shoppingList", shoppingList);
        model.addAttribute("name", session.getAttribute("name"));
        
        return "shopping_list";
    }
} 