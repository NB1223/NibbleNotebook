package com.example.nibblenotebook.repository;

import com.example.nibblenotebook.model.User;
import com.example.nibblenotebook.model.UserIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    
    @Query("SELECT ui FROM UserIngredient ui WHERE ui.user.id = :userId")
    List<UserIngredient> findPantryItemsForUser(@Param("userId") Integer userId);
}
