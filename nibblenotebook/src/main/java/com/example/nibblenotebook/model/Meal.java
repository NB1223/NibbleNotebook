package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Meal")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_id")
    private int id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealTime time;

    @ManyToMany
    @JoinTable(
        name = "Meal_Recipes",
        joinColumns = @JoinColumn(name = "meal_id"),
        inverseJoinColumns = @JoinColumn(name = "recipe_id")
    )
    private List<Recipe> recipes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public enum MealTime {
        Breakfast, Lunch, Dinner, Snack
    }

    public Meal() {}

    public static MealBuilder builder() {
        return new MealBuilder();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public MealTime getTime() { return time; }
    public void setTime(MealTime time) { this.time = time; }
    public List<Recipe> getRecipes() { return recipes; }
    public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    
    public void addRecipe(Recipe recipe) {
        if (this.recipes == null) {
            this.recipes = new ArrayList<>();
        }
        this.recipes.add(recipe);
    }
    
    public void removeRecipe(Recipe recipe) {
        if (this.recipes != null) {
            this.recipes.remove(recipe);
        }
    }
} 