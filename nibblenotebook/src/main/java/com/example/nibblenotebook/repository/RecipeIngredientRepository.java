package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Recipe;
import com.example.nibblenotebook.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Integer> {
    List<RecipeIngredient> findByRecipe(Recipe recipe);
    void deleteByRecipe(Recipe recipe);
    Optional<RecipeIngredient> findByRecipeAndIngredient_Id(Recipe recipe, int ingredientId);
}