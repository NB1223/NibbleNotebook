package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.Meal;
import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.repository.MealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/my-meals")
public class MealController {

    @Autowired
    private MealRepository mealRepository;

    // Display all meals for the logged-in user (if you want to display all meals)
    @GetMapping
    public String viewAllMeals(Model model) {
        List<Meal> meals = mealRepository.findAll(); // Fetch all meals from DB
        model.addAttribute("meals", meals);
        return "my-meals"; // Thymeleaf template for displaying all meals
    }

    // Display the list of available meals based on selected time (Breakfast, Lunch, etc.)
    @GetMapping("/select-time")
    public String selectMealTime(Model model) {
        model.addAttribute("mealTimes", List.of("Breakfast", "Lunch", "Dinner", "Snack"));
        return "my-meals"; // Thymeleaf template for selecting meal times
    }

    // Fetch and display meals based on the selected meal time
    @PostMapping("/select-time")
    public String displayMeals(@RequestParam("mealTime") String mealTime, Model model) {
        List<Meal> meals = mealRepository.findByTime(mealTime); // Fetch meals from DB based on selected time
        model.addAttribute("meals", meals);
        return "my-meals"; // Thymeleaf template for displaying the meal list
    }

    @GetMapping("/view/{mealId}")
public String viewMeal(@PathVariable("mealId") int mealId, Model model) {
    Meal meal = mealRepository.findById(mealId).orElse(null);
    if (meal != null) {
        List<Recipe> recipes = meal.getRecipes(); // Get associated recipes for the meal
        if (!recipes.isEmpty()) {
            model.addAttribute("recipe", recipes.get(0)); // Only one recipe
        } else {
            model.addAttribute("error", "No recipes found for this meal.");
        }
        model.addAttribute("meal", meal);
        return "view-meal"; // Thymeleaf template for meal details and one recipe
    } else {
        model.addAttribute("error", "Meal not found.");
        return "my-meals"; // Return to meal list if meal not found
    }
}

}
