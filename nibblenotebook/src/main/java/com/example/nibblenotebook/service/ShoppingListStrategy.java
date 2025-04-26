package com.example.nibblenotebook.service;

import com.example.nibblenotebook.model.ShoppingList;
import com.example.nibblenotebook.model.User;

public interface ShoppingListStrategy {
    ShoppingList generateShoppingList(User user, Object source);
}