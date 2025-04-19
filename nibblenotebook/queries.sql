CREATE DATABASE recipe_db;
USE recipe_db;

-- Ingredients Table (no quantity here)
CREATE TABLE Ingredients (
    ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    measurement_unit VARCHAR(50) NOT NULL
);

-- Tags Table (since it's referenced by Recipe)
CREATE TABLE Tags (
    tag_name VARCHAR(255) NOT NULL PRIMARY KEY
);

-- Recipe Table
CREATE TABLE Recipe (
    recipe_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cuisine VARCHAR(100),
    time DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Recipe_Ingredients Junction Table (many-to-many relationship)
CREATE TABLE Recipe_Ingredients (
    recipe_id INT,
    ingredient_id INT,
    quantity DOUBLE NOT NULL,  -- Track the quantity needed for the recipe
    PRIMARY KEY (recipe_id, ingredient_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id),
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id)
);

-- Recipe_Tags Junction Table
CREATE TABLE Recipe_Tags (
    recipe_id INT,
    tag_name VARCHAR(255),
    PRIMARY KEY (recipe_id, tag_name),
    FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id),
    FOREIGN KEY (tag_name) REFERENCES Tags(tag_name)
);

-- Recipe_Steps Table (one-to-many relationship)
CREATE TABLE Recipe_Steps (
    recipe_id INT,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id)
);

-- User Table
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

-- User_Saved_Recipes Junction Table
CREATE TABLE User_Saved_Recipes (
    user_id INT,
    recipe_id INT,
    PRIMARY KEY (user_id, recipe_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id)
);

-- User_Ingredients Table (many-to-many with quantity tracking)
CREATE TABLE User_Ingredients (
    user_ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    ingredient_id INT,
    quantity DOUBLE NOT NULL, -- Track the quantity of the ingredient for the user
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (ingredient_id) REFERENCES Ingredients(ingredient_id)
);

-- Meal Table
CREATE TABLE Meal (
    meal_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    time ENUM('Breakfast', 'Lunch', 'Dinner', 'Snack') NOT NULL
);

-- Meal_Recipes Junction Table
CREATE TABLE Meal_Recipes (
    meal_id INT,
    recipe_id INT,
    PRIMARY KEY (meal_id, recipe_id),
    FOREIGN KEY (meal_id) REFERENCES Meal(meal_id),
    FOREIGN KEY (recipe_id) REFERENCES Recipe(recipe_id)
);

-- Meal_Plan Table
CREATE TABLE Meal_Plan (
    meal_plan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    day ENUM('Mon', 'Tues', 'Wed', 'Thurs', 'Fri', 'Sat', 'Sun') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
);

-- Meal_Plan_Meals Junction Table
CREATE TABLE Meal_Plan_Meals (
    meal_plan_id INT,
    meal_id INT,
    PRIMARY KEY (meal_plan_id, meal_id),
    FOREIGN KEY (meal_plan_id) REFERENCES Meal_Plan(meal_plan_id),
    FOREIGN KEY (meal_id) REFERENCES Meal(meal_id)
);

-- User_Saved_Meals Table
CREATE TABLE User_Saved_Meals (
    user_id INT,
    meal_id INT,
    PRIMARY KEY (user_id, meal_id),
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    FOREIGN KEY (meal_id) REFERENCES Meal(meal_id)
);



-- insert ingredients otherwise pantry will not work
INSERT INTO Ingredients (name, measurement_unit) VALUES
('Flour', 'grams'),
('Sugar', 'grams'),
('Salt', 'grams'),
('Milk', 'ml'),
('Butter', 'grams'),
('Eggs', 'pieces'),
('Olive Oil', 'ml'),
('Garlic', 'cloves'),
('Tomatoes', 'pieces'),
('Rice', 'grams');
