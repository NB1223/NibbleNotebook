package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
}

