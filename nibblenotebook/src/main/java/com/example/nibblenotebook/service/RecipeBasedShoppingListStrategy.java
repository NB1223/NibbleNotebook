package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Transactional
public class RecipeBasedShoppingListStrategy implements ShoppingListStrategy {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ShoppingList generateShoppingList(User user, Object source) {
        if (!(source instanceof Recipe)) {
            throw new IllegalArgumentException("Source must be a Recipe");
        }
        
        Recipe recipe = (Recipe) source;
        
        List<RecipeIngredient> recipeIngredients = entityManager.createQuery(
            "SELECT ri FROM RecipeIngredient ri WHERE ri.recipe = :recipe", RecipeIngredient.class)
            .setParameter("recipe", recipe)
            .getResultList();
        
        List<UserIngredient> userIngredients = entityManager.createQuery(
            "SELECT ui FROM UserIngredient ui WHERE ui.recipe.user = :user", UserIngredient.class)
            .setParameter("user", user)
            .getResultList();
        
        ShoppingList shoppingList = new ShoppingList(user, "For: " + recipe.getName());
        entityManager.persist(shoppingList);
        
        for (RecipeIngredient recipeIngredient : recipeIngredients) {
            double neededQuantity = calculateNeededQuantity(recipeIngredient, userIngredients);
            
            if (neededQuantity > 0) {
                ShoppingItem item = new ShoppingItem(
                    shoppingList,
                    recipeIngredient.getIngredient(),
                    neededQuantity
                );
                entityManager.persist(item);
            }
        }
        
        return shoppingList;
    }

    private double calculateNeededQuantity(RecipeIngredient recipeIngredient, 
                                         List<UserIngredient> userIngredients) {
        return userIngredients.stream()
            .filter(ui -> ui.getIngredient().getId() == recipeIngredient.getIngredient().getId())
            .findFirst()
            .map(ui -> Math.max(0, recipeIngredient.getQuantity() - ui.getQuantity()))
            .orElse(recipeIngredient.getQuantity());
    }


    
}