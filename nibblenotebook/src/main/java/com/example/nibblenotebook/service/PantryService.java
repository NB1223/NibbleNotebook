package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.Ingredient;
import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.model.UserIngredient;
import java.util.List;

public interface PantryService {
    List<UserIngredient> getUserPantry(User user);
    UserIngredient addIngredientToPantry(User user, Ingredient ingredient, double quantity);
    UserIngredient updateIngredientQuantity(User user, int ingredientId, double newQuantity);
    void removeIngredientFromPantry(User user, int ingredientId);
    Ingredient createNewIngredient(String name, String measurementUnit);
    List<Ingredient> getAllIngredients(); // New method
}