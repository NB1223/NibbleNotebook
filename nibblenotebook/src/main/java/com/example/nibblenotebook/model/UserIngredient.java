package com.example.nibblenotebook.model;

import jakarta.persistence.*;

@Entity
@Table(name = "User_Ingredients")
public class UserIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_ingredient_id")
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;
    
    @Column(nullable = false)
    private double quantity;

    // Constructors, getters, and setters
    public UserIngredient() {}

    public UserIngredient(User user, Ingredient ingredient, double quantity) {
        this.user = user;
        this.ingredient = ingredient;
        this.quantity = quantity;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}