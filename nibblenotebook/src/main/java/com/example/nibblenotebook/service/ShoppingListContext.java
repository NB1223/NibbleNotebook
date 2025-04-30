package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Transactional
    public ShoppingList regenerateList(int listId, int userId) {
        ShoppingList list = entityManager.createQuery(
            "SELECT sl FROM ShoppingList sl WHERE sl.listId = :listId AND sl.user.id = :userId", 
            ShoppingList.class)
            .setParameter("listId", listId)
            .setParameter("userId", userId)
            .getSingleResult();

        if (list.getName().startsWith("For: ")) {
            String recipeName = list.getName().substring(5);
            
            Recipe recipe = entityManager.createQuery(
                "SELECT r FROM Recipe r WHERE r.user.id = :userId AND r.name = :name",
                Recipe.class)
                .setParameter("userId", userId)
                .setParameter("name", recipeName)
                .getSingleResult();

            entityManager.createQuery("DELETE FROM ShoppingItem i WHERE i.shoppingList = :list")
                    .setParameter("list", list)
                    .executeUpdate();

            List<RecipeIngredient> recipeIngredients = entityManager.createQuery(
                "SELECT ri FROM RecipeIngredient ri WHERE ri.recipe = :recipe", 
                RecipeIngredient.class)
                .setParameter("recipe", recipe)
                .getResultList();

            Map<Ingredient, Double> pantryQuantities = getPantryQuantities(userId);

            for (RecipeIngredient ri : recipeIngredients) {
                double neededQty = calculateNeededQuantity(ri, pantryQuantities);
                if (neededQty > 0) {
                    ShoppingItem item = new ShoppingItem(list, ri.getIngredient(), neededQty);
                    entityManager.persist(item);
                }
            }
        }
        
        entityManager.refresh(list);
        return list;
    }

    private Map<Ingredient, Double> getPantryQuantities(int userId) {
        List<UserIngredient> pantryItems = entityManager.createQuery(
            "SELECT ui FROM UserIngredient ui JOIN ui.recipe r " +
            "WHERE r.user.id = :userId AND r.name LIKE 'User Ingredient:%'",
            UserIngredient.class)
            .setParameter("userId", userId)
            .getResultList();
        
        return pantryItems.stream()
            .collect(Collectors.toMap(
                UserIngredient::getIngredient,
                UserIngredient::getQuantity,
                Double::sum
            ));
    }

    private double calculateNeededQuantity(RecipeIngredient recipeIngredient, 
                                        Map<Ingredient, Double> pantryQuantities) {
        Double available = pantryQuantities.get(recipeIngredient.getIngredient());
        if (available != null) {
            return Math.max(0, recipeIngredient.getQuantity() - available);
        }
        return recipeIngredient.getQuantity();
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