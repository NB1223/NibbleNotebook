package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.*;
import com.example.nibblenotebook.service.PantryService;
import com.example.nibblenotebook.service.ShoppingListContext;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/shopping-lists")
public class ShoppingListController {
    private final ShoppingListContext shoppingListContext;

    @Autowired
    public ShoppingListController(ShoppingListContext shoppingListContext) {
        this.shoppingListContext = shoppingListContext;
    }

    @GetMapping
    public String viewUserShoppingLists(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        
        List<ShoppingList> shoppingLists = entityManager.createQuery(
            "SELECT sl FROM ShoppingList sl WHERE sl.user = :user ORDER BY sl.createdAt DESC", 
            ShoppingList.class)
            .setParameter("user", user)
            .getResultList();
        
        model.addAttribute("shoppingLists", shoppingLists);
        
        List<Recipe> recipes = entityManager.createQuery(
            "SELECT r FROM Recipe r WHERE r.user = :user", Recipe.class)
            .setParameter("user", user)
            .getResultList();
        
        model.addAttribute("recipes", recipes);
        return "shopping-lists";
    }

    @PostMapping("/from-recipe")
    public String createFromRecipe(@RequestParam int recipeId, 
                                 HttpSession session) {
        User user = getAuthenticatedUser(session);
        Recipe recipe = entityManager.find(Recipe.class, recipeId);
        
        ShoppingList shoppingList = shoppingListContext.generateFromRecipe(user, recipe);
        return "redirect:/shopping-lists/" + shoppingList.getListId();
    }

    @PostMapping("/manual")
    public String createManualList(@RequestParam String listName,
                                 HttpSession session) {
        User user = getAuthenticatedUser(session);
        ShoppingList shoppingList = shoppingListContext.createManualList(user, listName);
        return "redirect:/shopping-lists/" + shoppingList.getListId();
    }
    
    @GetMapping("/{id}")
    public String viewShoppingList(@PathVariable int id,
                                 HttpSession session,
                                 Model model) {
        User user = getAuthenticatedUser(session);
        
        ShoppingList shoppingList = shoppingListContext.regenerateList(id, user.getId());
        
        model.addAttribute("shoppingList", shoppingList);
        model.addAttribute("allIngredients", shoppingListContext.getAllIngredients());
        model.addAttribute("isManualList", !shoppingList.getName().startsWith("For: "));
        return "shopping-list";
    }

    @PostMapping("/{id}/items")
    public String addItemToList(@PathVariable int id,
                              @RequestParam int ingredientId,
                              @RequestParam double quantity,
                              HttpSession session) {
        User user = getAuthenticatedUser(session);
        shoppingListContext.addItemToList(id, user.getId(), ingredientId, quantity);
        return "redirect:/shopping-lists/" + id;
    }

    @Transactional
    @PostMapping("/{id}/delete")
    public String deleteShoppingList(@PathVariable int id, HttpSession session) {
        User user = getAuthenticatedUser(session);
        
        ShoppingList shoppingList = entityManager.createQuery(
            "SELECT sl FROM ShoppingList sl WHERE sl.listId = :listId AND sl.user.id = :userId",
            ShoppingList.class)
            .setParameter("listId", id)
            .setParameter("userId", user.getId())
            .getSingleResult();
        
        if (shoppingList != null) {
            entityManager.remove(shoppingList);
        }
        
        return "redirect:/shopping-lists";
    }
    

    @PostMapping("/{id}/items/{itemId}/toggle")
    public String toggleItemPurchased(@PathVariable int id,
                                    @PathVariable int itemId,
                                    HttpSession session) {
        User user = getAuthenticatedUser(session);
        shoppingListContext.toggleItemPurchased(id, user.getId(), itemId);
        return "redirect:/shopping-lists/" + id;
    }

    private User getAuthenticatedUser(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            throw new SecurityException("User not authenticated");
        }
        User user = new User();
        user.setId(userId);
        return user;
    }
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PantryService pantryService;

    @GetMapping("/pantry")
    public String viewPantry(HttpSession session, Model model) {
        User user = getAuthenticatedUser(session);
        model.addAttribute("pantryItems", pantryService.getUserPantry(user));
        model.addAttribute("allIngredients", shoppingListContext.getAllIngredients());
        return "pantry";
    }

    @PostMapping("/pantry/add")
    public String addToPantry(@RequestParam int ingredientId,
                             @RequestParam double quantity,
                             HttpSession session) {
        User user = getAuthenticatedUser(session);
        Ingredient ingredient = entityManager.find(Ingredient.class, ingredientId);
        pantryService.addToPantry(user, ingredient, quantity);
        return "redirect:/pantry";
    }
    
    @PostMapping("/pantry/remove")
    public String removeFromPantry(@RequestParam int ingredientId,
                                 HttpSession session) {
        User user = getAuthenticatedUser(session);
        pantryService.removeFromPantry(user, ingredientId);
        return "redirect:/pantry";
    }
    
    @PostMapping("/pantry/update")
    public String updateQuantity(@RequestParam int ingredientId,
                               @RequestParam double newQuantity,
                               HttpSession session) {
        User user = getAuthenticatedUser(session);
        pantryService.updateQuantity(user, ingredientId, newQuantity);
        return "redirect:/pantry";
    }

    
}