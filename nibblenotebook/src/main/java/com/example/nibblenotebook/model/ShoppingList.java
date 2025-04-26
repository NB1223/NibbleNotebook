package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shopping_list")
public class ShoppingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private int listId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private String name;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingItem> items;

    public ShoppingList() {}

    public ShoppingList(User user, String name) {
        this.user = user;
        this.name = name;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<ShoppingItem> getItems() { return items; }
    public void setItems(List<ShoppingItem> items) { this.items = items; }
}