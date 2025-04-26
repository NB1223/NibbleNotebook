package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@Transactional
public class ManualShoppingListStrategy implements ShoppingListStrategy {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ShoppingList generateShoppingList(User user, Object source) {
        String listName = (String) source;
        ShoppingList shoppingList = new ShoppingList(user, listName);
        entityManager.persist(shoppingList);
        return shoppingList;
    }
}