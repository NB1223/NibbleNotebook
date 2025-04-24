package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.RecipeIngredient;
import com.example.nibblenotebook.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RecipeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Recipe> findByUser(User user) {
        String jpql = "SELECT r FROM Recipe r WHERE r.user = :user";
        TypedQuery<Recipe> query = entityManager.createQuery(jpql, Recipe.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    public Recipe findByIdAndUser(int id, User user) {
        String jpql = "SELECT r FROM Recipe r WHERE r.id = :id AND r.user = :user";
        TypedQuery<Recipe> query = entityManager.createQuery(jpql, Recipe.class);
        query.setParameter("id", id);
        query.setParameter("user", user);
        List<Recipe> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Recipe> findByNameContainingIgnoreCase(String name) {
        String jpql = "SELECT r FROM Recipe r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))";
        TypedQuery<Recipe> query = entityManager.createQuery(jpql, Recipe.class);
        query.setParameter("name", name);
        return query.getResultList();
    }

    public List<Recipe> findByCuisineContainingIgnoreCase(String cuisine) {
        String jpql = "SELECT r FROM Recipe r WHERE LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :cuisine, '%'))";
        TypedQuery<Recipe> query = entityManager.createQuery(jpql, Recipe.class);
        query.setParameter("cuisine", cuisine);
        return query.getResultList();
    }

    public List<String> findDistinctCuisines() {
        String jpql = "SELECT DISTINCT r.cuisine FROM Recipe r ORDER BY r.cuisine";
        TypedQuery<String> query = entityManager.createQuery(jpql, String.class);
        return query.getResultList();
    }

    public List<RecipeIngredient> findIngredientsForRecipe(int recipeId) {
        String jpql = "SELECT ri FROM RecipeIngredient ri WHERE ri.recipe.id = :recipeId";
        TypedQuery<RecipeIngredient> query = entityManager.createQuery(jpql, RecipeIngredient.class);
        query.setParameter("recipeId", recipeId);
        return query.getResultList();
    }

    public void save(Recipe recipe) {
        if (recipe.getId() == 0) {
            entityManager.persist(recipe);
        } else {
            entityManager.merge(recipe);
        }
    }

    public Recipe findById(int id) {
        return entityManager.find(Recipe.class, id);
    }

    public List<Recipe> findAll() {
        String jpql = "SELECT r FROM Recipe r";
        return entityManager.createQuery(jpql, Recipe.class).getResultList();
    }

    public void deleteById(int id) {
        Recipe recipe = entityManager.find(Recipe.class, id);
        if (recipe != null) {
            entityManager.remove(recipe);
        }
    }
}
