package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.Ingredient;
import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.model.UserIngredient;
import java.util.List;
import java.util.Map;

public interface PantryService {
    List<UserIngredient> getUserPantry(User user);
    
    UserIngredient addToPantry(User user, Ingredient ingredient, double quantity);
    void removeFromPantry(User user, int ingredientId);
    UserIngredient updateQuantity(User user, int ingredientId, double newQuantity);

    Ingredient createNewIngredient(String name, String measurementUnit);
    List<Ingredient> getAllIngredients();
    Map<Ingredient, Double> getPantryQuantities(User user);
}