package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class ShoppingListContext {
    @PersistenceContext
    private EntityManager entityManager;
    
    private final RecipeBasedShoppingListStrategy recipeStrategy;
    private final ManualShoppingListStrategy manualStrategy;

    @Autowired
    public ShoppingListContext(RecipeBasedShoppingListStrategy recipeStrategy,
                             ManualShoppingListStrategy manualStrategy) {
        this.recipeStrategy = recipeStrategy;
        this.manualStrategy = manualStrategy;
    }

    public ShoppingList generateFromRecipe(User user, Recipe recipe) {
        return recipeStrategy.generateShoppingList(user, recipe);
    }

    public ShoppingList createManualList(User user, String listName) {
        return manualStrategy.generateShoppingList(user, listName);
    }

    public ShoppingList getShoppingList(int listId, int userId) {
        return entityManager.createQuery(
            "SELECT sl FROM ShoppingList sl WHERE sl.listId = :listId AND sl.user.id = :userId", 
            ShoppingList.class)
            .setParameter("listId", listId)
            .setParameter("userId", userId)
            .getSingleResult();
    }

    public List<Ingredient> getAllIngredients() {
        return entityManager.createQuery(
            "SELECT i FROM Ingredient i ORDER BY i.name", Ingredient.class)
            .getResultList();
    }

    public void addItemToList(int listId, int userId, int ingredientId, double quantity) {
        ShoppingList list = getShoppingList(listId, userId);
        Ingredient ingredient = entityManager.find(Ingredient.class, ingredientId);
        
        ShoppingItem item = new ShoppingItem(list, ingredient, quantity);
        entityManager.persist(item);
    }

    public void toggleItemPurchased(int listId, int userId, int itemId) {
        ShoppingItem item = entityManager.createQuery(
            "SELECT sli FROM ShoppingItem sli " +
            "WHERE sli.itemId = :itemId AND sli.shoppingList.listId = :listId " +
            "AND sli.shoppingList.user.id = :userId", ShoppingItem.class)
            .setParameter("itemId", itemId)
            .setParameter("listId", listId)
            .setParameter("userId", userId)
            .getSingleResult();
        
        item.setPurchased(!item.isPurchased());
        entityManager.merge(item);
    }
}