package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.model.UserIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User findByUsername(String username) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        List<User> users = query.getResultList();
        return users.isEmpty() ? null : users.get(0);
    }

    public List<UserIngredient> findPantryItemsForUser(Integer userId) {
        String jpql = "SELECT ui FROM UserIngredient ui WHERE ui.user.id = :userId";
        TypedQuery<UserIngredient> query = entityManager.createQuery(jpql, UserIngredient.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    public User findById(int id) {
        return entityManager.find(User.class, id);
    }

    public void save(User user) {
        if (user.getId() == 0) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }

    public List<User> findAll() {
        String jpql = "SELECT u FROM User u";
        return entityManager.createQuery(jpql, User.class).getResultList();
    }

    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }
}
