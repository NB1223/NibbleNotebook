package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByUser(User user);
    Recipe findByIdAndUser(int id, User user);
    List<Recipe> findByNameContainingIgnoreCase(String name);
    List<Recipe> findByCuisineContainingIgnoreCase(String cuisine);
    
    @Query("SELECT DISTINCT r.cuisine FROM Recipe r ORDER BY r.cuisine")
    List<String> findDistinctCuisines();
}