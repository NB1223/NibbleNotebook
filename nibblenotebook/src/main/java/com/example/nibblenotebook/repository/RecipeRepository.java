package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
    List<Recipe> findByUser(User user);
    Recipe findByIdAndUser(int id, User user);
}