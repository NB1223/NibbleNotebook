package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.model.UserIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserIngredientRepository extends JpaRepository<UserIngredient, Integer> {
    List<UserIngredient> findByUser(User user);
    Optional<UserIngredient> findByUserAndIngredient_Id(User user, int ingredientId);
}
