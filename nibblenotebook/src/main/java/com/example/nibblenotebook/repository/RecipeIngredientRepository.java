package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.RecipeIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RecipeIngredientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RecipeIngredient> findByRecipe(Recipe recipe) {
        String jpql = "SELECT ri FROM RecipeIngredient ri WHERE ri.recipe = :recipe";
        TypedQuery<RecipeIngredient> query = entityManager.createQuery(jpql, RecipeIngredient.class);
        query.setParameter("recipe", recipe);
        return query.getResultList();
    }

    public void deleteByRecipe(Recipe recipe) {
        String jpql = "DELETE FROM RecipeIngredient ri WHERE ri.recipe = :recipe";
        entityManager.createQuery(jpql)
                     .setParameter("recipe", recipe)
                     .executeUpdate();
    }

    public Optional<RecipeIngredient> findByRecipeAndIngredientId(Recipe recipe, int ingredientId) {
        String jpql = "SELECT ri FROM RecipeIngredient ri WHERE ri.recipe = :recipe AND ri.ingredient.id = :ingredientId";
        TypedQuery<RecipeIngredient> query = entityManager.createQuery(jpql, RecipeIngredient.class);
        query.setParameter("recipe", recipe);
        query.setParameter("ingredientId", ingredientId);
        List<RecipeIngredient> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    public void save(RecipeIngredient ri) {
        if (ri.getRecipeIngredientId() == 0) {
            entityManager.persist(ri);
        } else {
            entityManager.merge(ri);
        }
    }

    public RecipeIngredient findById(int id) {
        return entityManager.find(RecipeIngredient.class, id);
    }

    public List<RecipeIngredient> findAll() {
        String jpql = "SELECT ri FROM RecipeIngredient ri";
        return entityManager.createQuery(jpql, RecipeIngredient.class).getResultList();
    }

    public void deleteById(int id) {
        RecipeIngredient ri = entityManager.find(RecipeIngredient.class, id);
        if (ri != null) {
            entityManager.remove(ri);
        }
    }
}
