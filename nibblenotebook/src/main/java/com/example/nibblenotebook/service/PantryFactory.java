package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.Ingredient;
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
            "SELECT ui FROM UserIngredient ui WHERE ui.user = :user", UserIngredient.class)
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
        UserIngredient userIngredient = new UserIngredient(user, ingredient, quantity);
        entityManager.persist(userIngredient);
        return userIngredient;
    }

    @Override
    public UserIngredient updateIngredientQuantity(User user, int ingredientId, double newQuantity) {
        UserIngredient userIngredient = entityManager.createQuery(
            "SELECT ui FROM UserIngredient ui WHERE ui.user = :user AND ui.ingredient.id = :ingredientId", 
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
            "SELECT ui FROM UserIngredient ui WHERE ui.user = :user AND ui.ingredient.id = :ingredientId", 
            UserIngredient.class)
            .setParameter("user", user)
            .setParameter("ingredientId", ingredientId)
            .getSingleResult();
        
        entityManager.remove(userIngredient);
    }

    @Override
    public Ingredient createNewIngredient(String name, String measurementUnit) {
        Ingredient ingredient = new Ingredient(name, measurementUnit);
        entityManager.persist(ingredient);
        return ingredient;
    }
}