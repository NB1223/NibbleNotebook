package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.*;
import com.example.nibblenotebook.repository.*;
import com.example.nibblenotebook.service.PantryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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
    private PantryService pantryService;
    
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
        
        List<UserIngredient> pantryItems = pantryService.getUserPantry(user);
        
        if (pantryItems != null && !pantryItems.isEmpty()) {
            List<User.ShoppingListItem> itemsToRemove = new ArrayList<>();
            
            for (UserIngredient ui : pantryItems) {
                if (ui != null && ui.getIngredient() != null) {
                    for (User.ShoppingListItem item : shoppingList) {
                        if (item.getIngredient().getId() == ui.getIngredient().getId()) {
                            double newQuantity = item.getQuantity() - ui.getQuantity();
                            if (newQuantity <= 0) {
                                itemsToRemove.add(item);
                            } else {
                                item.setQuantity(newQuantity);
                            }
                            break;
                        }
                    }
                }
            }
            
            shoppingList.removeAll(itemsToRemove);
        }
        
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
        
        List<UserIngredient> pantry = pantryService.getUserPantry(user);
        
        return pantry.stream().map(ui -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ingredient", ui.getIngredient().getName());
            map.put("quantity", ui.getQuantity());
            map.put("unit", ui.getIngredient().getMeasurementUnit());
            return map;
        }).toList();
    }
} 