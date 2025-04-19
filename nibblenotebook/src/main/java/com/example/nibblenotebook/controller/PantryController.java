package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.*;
import com.example.nibblenotebook.repository.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PantryController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private IngredientRepository ingredientRepo;

    @Autowired
    private UserIngredientRepository userIngredientRepo;

    @GetMapping("/my-pantry")
    public String showPantry(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepo.findById(userId).orElse(null);
        List<UserIngredient> pantry = userIngredientRepo.findByUser(user);
        List<Ingredient> allIngredients = ingredientRepo.findAll();

        model.addAttribute("pantry", pantry);
        model.addAttribute("allIngredients", allIngredients);
        model.addAttribute("userIngredient", new UserIngredient());

        return "my-pantry";
    }

    @PostMapping("/my-pantry/add")
    public String addIngredient(HttpSession session,
                                @RequestParam int ingredientId,
                                @RequestParam double quantity) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        User user = userRepo.findById(userId).orElse(null);
        Ingredient ingredient = ingredientRepo.findById(ingredientId).orElse(null);

        if (user != null && ingredient != null) {
            UserIngredient existing = userIngredientRepo.findByUserAndIngredient_Id(user, ingredientId).orElse(null);
            if (existing != null) {
                existing.setQuantity(quantity);
                userIngredientRepo.save(existing);
            } else {
                userIngredientRepo.save(new UserIngredient(user, ingredient, quantity));
            }
        }

        return "redirect:/my-pantry";
    }

    @PostMapping("/my-pantry/delete")
    public String deleteIngredient(@RequestParam int userIngredientId) {
        userIngredientRepo.deleteById(userIngredientId);
        return "redirect:/my-pantry";
    }
}
