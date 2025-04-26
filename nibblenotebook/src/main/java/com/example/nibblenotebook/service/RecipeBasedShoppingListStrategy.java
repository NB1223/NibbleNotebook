package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Transactional
public class RecipeBasedShoppingListStrategy implements ShoppingListStrategy {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private PantryFactory pantryFactory;

    @Override
    public ShoppingList generateShoppingList(User user, Object source) {
        if (!(source instanceof Recipe)) {
            throw new IllegalArgumentException("Source must be a Recipe");
        }
        
        Recipe recipe = (Recipe) source;
        
        // 1. Get all ingredients required by the recipe
        List<RecipeIngredient> recipeIngredients = entityManager.createQuery(
            "SELECT ri FROM RecipeIngredient ri WHERE ri.recipe = :recipe", RecipeIngredient.class)
            .setParameter("recipe", recipe)
            .getResultList();
        
        // 2. Get user's pantry quantities
        Map<Ingredient, Double> pantryQuantities = pantryFactory.getPantryQuantities(user);
        
        // 3. Create shopping list
        ShoppingList shoppingList = new ShoppingList(user, "For: " + recipe.getName());
        entityManager.persist(shoppingList);
        
        // 4. Calculate needed items
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            Ingredient ingredient = recipeIngredient.getIngredient();
            double neededQty = recipeIngredient.getQuantity();
            
            // Subtract what's available in pantry
            if (pantryQuantities.containsKey(ingredient)) {
                neededQty = Math.max(0, neededQty - pantryQuantities.get(ingredient));
            }
            
            if (neededQty > 0) {
                ShoppingItem item = new ShoppingItem(
                    shoppingList,
                    ingredient,
                    neededQty
                );
                entityManager.persist(item);
            }
        }
        
        return shoppingList;
    }
}