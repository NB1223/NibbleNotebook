package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.Ingredient;
import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.model.UserIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class PantryFactory implements PantryService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<UserIngredient> getUserPantry(User user) {
        return entityManager.createQuery(
            "SELECT ri FROM UserIngredient ri JOIN ri.recipe r WHERE r.user = :user AND r.name LIKE 'User Ingredient:%'", 
            UserIngredient.class)
            .setParameter("user", user)
            .getResultList();
    }

    @Override
    public List<Ingredient> getAllIngredients() {
        return entityManager.createQuery(
            "SELECT i FROM Ingredient i ORDER BY i.name", Ingredient.class)
            .getResultList();
    }

    @Override
    public UserIngredient addIngredientToPantry(User user, Ingredient ingredient, double quantity) {
        // Check if a pantry recipe already exists for this user
        List<Recipe> pantryRecipes = entityManager.createQuery(
            "SELECT r FROM Recipe r WHERE r.user = :user AND r.name = :name", 
            Recipe.class)
            .setParameter("user", user)
            .setParameter("name", "User Ingredient: " + ingredient.getName())
            .getResultList();
        
        Recipe pantryRecipe;
        if (pantryRecipes.isEmpty()) {
            // Create a new recipe for this ingredient
            pantryRecipe = new Recipe();
            pantryRecipe.setUser(user);
            pantryRecipe.setName("User Ingredient: " + ingredient.getName());
            entityManager.persist(pantryRecipe);
        } else {
            pantryRecipe = pantryRecipes.get(0);
        }
        
        // Create and save the ingredient
        UserIngredient userIngredient = new UserIngredient(pantryRecipe, ingredient, quantity);
        entityManager.persist(userIngredient);
        return userIngredient;
    }

    @Override
    public UserIngredient updateIngredientQuantity(User user, int ingredientId, double newQuantity) {
        UserIngredient userIngredient = entityManager.createQuery(
            "SELECT ri FROM UserIngredient ri JOIN ri.recipe r WHERE r.user = :user AND ri.ingredient.id = :ingredientId AND r.name LIKE 'User Ingredient:%'", 
            UserIngredient.class)
            .setParameter("user", user)
            .setParameter("ingredientId", ingredientId)
            .getSingleResult();
        
        userIngredient.setQuantity(newQuantity);
        return entityManager.merge(userIngredient);
    }

    @Override
    public void removeIngredientFromPantry(User user, int ingredientId) {
        UserIngredient userIngredient = entityManager.createQuery(
            "SELECT ri FROM UserIngredient ri JOIN ri.recipe r WHERE r.user = :user AND ri.ingredient.id = :ingredientId AND r.name LIKE 'User Ingredient:%'", 
            UserIngredient.class)
            .setParameter("user", user)
            .setParameter("ingredientId", ingredientId)
            .getSingleResult();
        
        // Delete the associated recipe if this is the only ingredient
        Recipe recipe = userIngredient.getRecipe();
        entityManager.remove(userIngredient);
        
        // Check if this was the only ingredient in the recipe
        long count = entityManager.createQuery(
            "SELECT COUNT(ri) FROM UserIngredient ri WHERE ri.recipe = :recipe", 
            Long.class)
            .setParameter("recipe", recipe)
            .getSingleResult();
            
        if (count == 0) {
            entityManager.remove(recipe);
        }
    }

    @Override
    public Ingredient createNewIngredient(String name, String measurementUnit) {
        Ingredient ingredient = new Ingredient(name, measurementUnit);
        entityManager.persist(ingredient);
        return ingredient;
    }
}