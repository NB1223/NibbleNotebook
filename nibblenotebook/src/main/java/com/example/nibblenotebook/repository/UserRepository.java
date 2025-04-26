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
        try {
            System.out.println("Looking for user with username: " + username);
            
            // First try exact match with JPQL
            String jpql = "SELECT u FROM User u WHERE u.username = :username";
            TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
            query.setParameter("username", username);
            List<User> users = query.getResultList();
            
            if (!users.isEmpty()) {
                System.out.println("Found user with JPQL: " + users.get(0).getUsername());
                return users.get(0);
            }
            
            // If no user found with JPQL, try native SQL query
            System.out.println("No user found with JPQL, trying native SQL");
            List<User> nativeUsers = entityManager
                .createNativeQuery("SELECT * FROM User WHERE user_name = ?", User.class)
                .setParameter(1, username)
                .getResultList();
                
            if (!nativeUsers.isEmpty()) {
                System.out.println("Found user with native SQL: " + nativeUsers.get(0).getUsername());
                return nativeUsers.get(0);
            }
            
            // If still not found, log all users to debug
            System.out.println("Still no user found, listing all users in database:");
            List<User> allUsers = entityManager
                .createNativeQuery("SELECT * FROM User", User.class)
                .getResultList();
                
            for (User user : allUsers) {
                System.out.println("  DB User: " + user.getUsername() + ", ID: " + user.getId());
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Error in findByUsername: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<UserIngredient> findPantryItemsForUser(Integer userId) {
        String jpql = "SELECT ri FROM UserIngredient ri JOIN ri.recipe r WHERE r.user.id = :userId AND r.name LIKE 'User Ingredient:%'";
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
