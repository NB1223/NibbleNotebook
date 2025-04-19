package com.example.nibblenotebook.controller;

import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        if (userRepo.findByUsername(user.getUsername()) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        userRepo.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("user") User user, Model model, HttpSession session) {
        User dbUser = userRepo.findByUsername(user.getUsername());

        if (dbUser == null || !dbUser.getPassword().equals(user.getPassword())) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }

        // Debug print
        System.out.println("Logging in: " + dbUser.getName() + " (ID: " + dbUser.getId() + ")");

        // Store in session
        session.setAttribute("userId", dbUser.getId());
        session.setAttribute("username", dbUser.getUsername());
        session.setAttribute("name", dbUser.getName());

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        String name = (String) session.getAttribute("name");
    
        System.out.println("Home - Session userId: " + userId);
        System.out.println("Home - Session name: " + name);
    
        if (userId == null) {
            return "redirect:/login";
        }
    
        model.addAttribute("userId", userId);
        model.addAttribute("name", name);
    
        return "home";
    }    

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}