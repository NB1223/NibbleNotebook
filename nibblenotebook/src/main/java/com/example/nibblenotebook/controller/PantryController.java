package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.Ingredient;
import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.model.UserIngredient;
import com.example.nibblenotebook.service.PantryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/my-pantry")
public class PantryController {

    @Autowired
    private PantryService pantryService;

    @GetMapping
    public String viewPantry(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
    
        User user = new User();
        user.setId(userId);
        
        // Existing code
        List<UserIngredient> pantryItems = pantryService.getUserPantry(user);
        model.addAttribute("pantryItems", pantryItems);
        model.addAttribute("newIngredient", new Ingredient());
        
        // New code - just add this line
        model.addAttribute("allIngredients", pantryService.getAllIngredients());
        
        return "pantry";
    }

    @PostMapping("/add-existing")
    public String addExistingIngredient(@RequestParam int ingredientId, 
                                      @RequestParam double quantity,
                                      HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = new User();
        user.setId(userId);
        
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientId);
        
        pantryService.addToPantry(user, ingredient, quantity);
        return "redirect:/my-pantry";
    }

    @PostMapping("/add-new")
    public String addNewIngredient(@ModelAttribute Ingredient newIngredient,
                                 @RequestParam double quantity,
                                 HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = new User();
        user.setId(userId);
        
        Ingredient createdIngredient = pantryService.createNewIngredient(
            newIngredient.getName(), 
            newIngredient.getMeasurementUnit());
        
        pantryService.addToPantry(user, createdIngredient, quantity);
        return "redirect:/my-pantry";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam int ingredientId,
                               @RequestParam double newQuantity,
                               HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = new User();
        user.setId(userId);
        
        pantryService.updateQuantity(user, ingredientId, newQuantity);
        return "redirect:/my-pantry";
    }

    @PostMapping("/remove")
    public String removeIngredient(@RequestParam int ingredientId,
                                 HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }

        User user = new User();
        user.setId(userId);
        
        pantryService.removeFromPantry(user, ingredientId);
        return "redirect:/my-pantry";
    }
}