package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Ingredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class IngredientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Ingredient findById(int id) {
        return entityManager.find(Ingredient.class, id);
    }

    public List<Ingredient> findAll() {
        String jpql = "SELECT i FROM Ingredient i";
        TypedQuery<Ingredient> query = entityManager.createQuery(jpql, Ingredient.class);
        return query.getResultList();
    }

    public void save(Ingredient ingredient) {
        if (ingredient.getId() == 0) {
            entityManager.persist(ingredient);
        } else {
            entityManager.merge(ingredient);
        }
    }

    public void deleteById(int id) {
        Ingredient ingredient = entityManager.find(Ingredient.class, id);
        if (ingredient != null) {
            entityManager.remove(ingredient);
        }
    }
}
