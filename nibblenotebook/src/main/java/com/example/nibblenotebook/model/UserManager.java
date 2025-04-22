package com.example.nibblenotebook.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.nibblenotebook.repository.UserRepository;
import java.util.List;
import java.util.Optional;

@Component
public class UserManager {
    
    @Autowired
    private UserRepository userRepository;
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get user by ID
    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }
    
    // Find user by username
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Save or update a user
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    // Delete a user
    public void deleteUser(int userId) {
        userRepository.deleteById(userId);
    }
} 