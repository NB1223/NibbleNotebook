package com.example.nibblenotebook.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tags")
public class Tag {
    
    @Id
    @Column(name = "tag_name")
    private String tagName;
    
    @ManyToMany(mappedBy = "tags")
    private List<Recipe> recipes = new ArrayList<>();
    
    // Constructors
    public Tag() {}
    
    public Tag(String tagName) {
        this.tagName = tagName;
    }
    
    // Getters and setters
    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }
    public List<Recipe> getRecipes() { return recipes; }
    public void setRecipes(List<Recipe> recipes) { this.recipes = recipes; }
}