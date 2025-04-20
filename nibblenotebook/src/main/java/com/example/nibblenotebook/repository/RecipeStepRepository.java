package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Integer> {
    List<RecipeStep> findByRecipeId(int recipeId);
    void deleteByRecipeId(int recipeId);
}