package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.RecipeStep;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class RecipeStepRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<RecipeStep> findByRecipe(Recipe recipe) {
        String jpql = "SELECT rs FROM RecipeStep rs WHERE rs.recipe = :recipe ORDER BY rs.stepNumber";
        TypedQuery<RecipeStep> query = entityManager.createQuery(jpql, RecipeStep.class);
        query.setParameter("recipe", recipe);
        return query.getResultList();
    }

    public void deleteByRecipe(Recipe recipe) {
        String jpql = "DELETE FROM RecipeStep rs WHERE rs.recipe = :recipe";
        entityManager.createQuery(jpql)
                     .setParameter("recipe", recipe)
                     .executeUpdate();
    }

    public void deleteById(int stepId) {
        String jpql = "DELETE FROM RecipeStep rs WHERE rs.id = :stepId";
        entityManager.createQuery(jpql)
                     .setParameter("stepId", stepId)
                     .executeUpdate();
    }

    public void save(RecipeStep step) {
        if (step.getId() == 0) {
            entityManager.persist(step);
        } else {
            entityManager.merge(step);
        }
    }

    public RecipeStep findById(int id) {
        return entityManager.find(RecipeStep.class, id);
    }

    public List<RecipeStep> findAll() {
        String jpql = "SELECT rs FROM RecipeStep rs ORDER BY rs.recipe.id, rs.stepNumber";
        return entityManager.createQuery(jpql, RecipeStep.class).getResultList();
    }
}
