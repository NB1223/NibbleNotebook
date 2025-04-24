package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.*;
import com.example.nibblenotebook.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MealPlanRepository mealPlanRepository;
    
    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private RecipeRepository recipeRepository;
    
    @Autowired
    private UserIngredientRepository userIngredientRepository;
    
    @GetMapping("/meal-plans")
    public List<MealPlan> getMealPlans(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return List.of();
        
        User user = userRepository.findById(userId);
        if (user == null) return List.of();
        
        return mealPlanRepository.findByUser(user);
    }
    
    @GetMapping("/shopping-list")
    public List<Map<String, Object>> getShoppingList(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return List.of();
        
        User user = userRepository.findById(userId);
        if (user == null) return List.of();
        
        List<User.ShoppingListItem> shoppingList = user.generateShoppingList();
        
        return shoppingList.stream().map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ingredient", item.getIngredient().getName());
            map.put("quantity", item.getQuantity());
            map.put("unit", item.getIngredient().getMeasurementUnit());
            return map;
        }).toList();
    }
    
    @GetMapping("/pantry")
    public List<Map<String, Object>> getPantry(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return List.of();
        
        User user = userRepository.findById(userId);
        if (user == null) return List.of();
        
        List<UserIngredient> pantry = userIngredientRepository.findByUser(user);
        
        return pantry.stream().map(ui -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ingredient", ui.getIngredient().getName());
            map.put("quantity", ui.getQuantity());
            map.put("unit", ui.getIngredient().getMeasurementUnit());
            return map;
        }).toList();
    }
} 