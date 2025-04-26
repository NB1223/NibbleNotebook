CREATE DATABASE IF NOT EXISTS recipe_db;
USE recipe_db;

-- Ingredients Table (no quantity here)
CREATE TABLE IF NOT EXISTS ingredients (
    ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    measurement_unit VARCHAR(50) NOT NULL
);

-- User Table
CREATE TABLE IF NOT EXISTS user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255) UNIQUE NOT NULL, 
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL
);

-- Recipe Table
CREATE TABLE IF NOT EXISTS recipe (
    recipe_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    cuisine VARCHAR(100),
    time DOUBLE,
    vegetarian BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Recipe_Ingredients Junction Table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS recipe_ingredients (
    recipe_ingredient_id INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id INT,
    ingredient_id INT,
    quantity DOUBLE NOT NULL, 
    FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
);

-- Recipe_Steps Table (one-to-many relationship)
CREATE TABLE IF NOT EXISTS recipe_steps (
    recipe_step_id INT AUTO_INCREMENT PRIMARY KEY,
    recipe_id INT,
    step_number INT NOT NULL,
    instruction TEXT NOT NULL,
    FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id)
);

-- Meal Table
CREATE TABLE IF NOT EXISTS meal (
    meal_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(255) NOT NULL,
    time ENUM('Breakfast', 'Lunch', 'Dinner', 'Snack') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Meal_Recipes Junction Table
CREATE TABLE IF NOT EXISTS meal_recipes (
    meal_id INT,
    recipe_id INT,
    PRIMARY KEY (meal_id, recipe_id),
    FOREIGN KEY (meal_id) REFERENCES meal(meal_id),
    FOREIGN KEY (recipe_id) REFERENCES recipe(recipe_id)
);

-- Meal_Plan Table
CREATE TABLE IF NOT EXISTS meal_plan (
    meal_plan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    day ENUM('Mon', 'Tues', 'Wed', 'Thurs', 'Fri', 'Sat', 'Sun') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Meal_Plan_Meals Junction Table
CREATE TABLE IF NOT EXISTS meal_plan_meals (
    meal_plan_id INT,
    meal_id INT,
    PRIMARY KEY (meal_plan_id, meal_id),
    FOREIGN KEY (meal_plan_id) REFERENCES meal_plan(meal_plan_id),
    FOREIGN KEY (meal_id) REFERENCES meal(meal_id)
);

-- Shopping List Table
CREATE TABLE IF NOT EXISTS shopping_list (
    list_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- Shopping List Items Table
CREATE TABLE IF NOT EXISTS shopping_list_items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    list_id INT,
    ingredient_id INT,
    quantity DOUBLE NOT NULL,
    purchased BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (list_id) REFERENCES shopping_list(list_id),
    FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id)
);

-- insert ingredients otherwise pantry will not work
INSERT INTO ingredients (name, measurement_unit) VALUES
('Flour', 'grams'),
('Sugar', 'grams'),
('Salt', 'grams'),
('Milk', 'ml'),
('Butter', 'grams'),
('Eggs', 'pieces'),
('Olive Oil', 'ml'),
('Garlic', 'cloves'),
('Tomatoes', 'pieces'),
('Rice', 'grams'),
('Chicken Breast', 'grams'),
('Pasta', 'grams'),
('Onion', 'pieces'),
('Cheese', 'grams'),
('Black Pepper', 'grams'),
('Vanilla Extract', 'ml'),
('Baking Powder', 'grams'),
('Chocolate Chips', 'grams'),
('Carrots', 'pieces'),
('Broccoli', 'grams'),
('Ground Beef', 'grams'),
('Bell Pepper', 'pieces'),
('Potatoes', 'grams'),
('Lemon', 'pieces'),
('Honey', 'ml');

INSERT INTO user (user_name, password, name) VALUES
('chef_john', 'hashed_password_1', 'John Smith'),
('baking_queen', 'hashed_password_2', 'Mary Johnson'),
('healthy_eater', 'hashed_password_3', 'David Wilson'),
('food_explorer', 'hashed_password_4', 'Sarah Lee'),
('weekend_cook', 'hashed_password_5', 'Michael Brown');

INSERT INTO recipe (user_id, name, description, cuisine, time, vegetarian) VALUES
(1, 'Classic Pancakes', 'Fluffy homemade pancakes perfect for weekend breakfast', 'American', 30, FALSE),
(2, 'Chocolate Chip Cookies', 'Chewy cookies with melty chocolate chips', 'American', 45, TRUE),
(3, 'Grilled Chicken Salad', 'Healthy salad with grilled chicken and fresh veggies', 'Mediterranean', 25, FALSE),
(4, 'Spaghetti Bolognese', 'Classic Italian pasta dish with rich meat sauce', 'Italian', 60, FALSE),
(1, 'Vegetable Stir Fry', 'Quick and healthy vegetable stir fry with rice', 'Asian', 20, TRUE),
(5, 'Mashed Potatoes', 'Creamy homemade mashed potatoes', 'American', 40, TRUE),
(2, 'Banana Bread', 'Moist and delicious banana bread', 'American', 75, TRUE),
(3, 'Greek Salad', 'Fresh salad with feta cheese and olives', 'Greek', 15, TRUE);

INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity) VALUES
-- Pancakes
(1, 1, 200), (1, 2, 50), (1, 3, 5), (1, 4, 250), (1, 5, 30), (1, 6, 2),
-- Chocolate Chip Cookies
(2, 1, 250), (2, 2, 150), (2, 5, 100), (2, 6, 2), (2, 7, 5), (2, 8, 10), (2, 11, 200),
-- Grilled Chicken Salad
(3, 12, 300), (3, 19, 2), (3, 10, 150), (3, 15, 100), (3, 9, 30), (3, 7, 15),
-- Spaghetti Bolognese
(4, 13, 400), (4, 16, 500), (4, 19, 1), (4, 9, 4), (4, 8, 3), (4, 3, 5), (4, 5, 10),
-- Vegetable Stir Fry
(5, 17, 300), (5, 19, 1), (5, 15, 2), (5, 10, 200), (5, 7, 30), (5, 3, 3),
-- Mashed Potatoes
(6, 18, 1000), (6, 4, 100), (6, 5, 50), (6, 3, 5),
-- Banana Bread
(7, 1, 300), (7, 2, 150), (7, 5, 100), (7, 6, 3), (7, 20, 3), (7, 8, 5),
-- Greek Salad
(8, 9, 4), (8, 14, 200), (8, 15, 1), (8, 7, 30), (8, 3, 5), (8, 10, 100);

INSERT INTO recipe_steps (recipe_id, step_number, instruction) VALUES
-- Pancakes
(1, 1, 'Mix dry ingredients (flour, sugar, salt, baking powder) in a large bowl'),
(1, 2, 'In another bowl, whisk together milk, melted butter, and eggs'),
(1, 3, 'Combine wet and dry ingredients, stirring until just combined'),
(1, 4, 'Heat a griddle over medium heat and pour 1/4 cup batter for each pancake'),
(1, 5, 'Cook until bubbles form on surface, then flip and cook until golden brown'),

-- Chocolate Chip Cookies
(2, 1, 'Preheat oven to 375°F (190°C)'),
(2, 2, 'Cream together butter and sugars until light and fluffy'),
(2, 3, 'Beat in eggs one at a time, then stir in vanilla'),
(2, 4, 'Combine flour, baking soda, and salt; gradually stir into the creamed mixture'),
(2, 5, 'Fold in chocolate chips'),
(2, 6, 'Drop by rounded tablespoon onto ungreased baking sheets'),
(2, 7, 'Bake for 9-11 minutes until golden brown'),

-- Grilled Chicken Salad
(3, 1, 'Season chicken breasts with salt and pepper'),
(3, 2, 'Grill chicken for 6-8 minutes per side until cooked through'),
(3, 3, 'Let chicken rest, then slice'),
(3, 4, 'Chop vegetables and arrange on plates'),
(3, 5, 'Top with sliced chicken and drizzle with olive oil and lemon juice');

-- Updated meal entries with user_id
INSERT INTO meal (user_id, name, time) VALUES
(1, 'Breakfast Combo', 'Breakfast'),
(2, 'Light Lunch', 'Lunch'),
(3, 'Family Dinner', 'Dinner'),
(4, 'Afternoon Snack', 'Snack'),
(5, 'Sunday Brunch', 'Breakfast'),
(1, 'Quick Dinner', 'Dinner');

INSERT INTO meal_recipes (meal_id, recipe_id) VALUES
(1, 1), (1, 7),  -- Breakfast Combo: Pancakes and Banana Bread
(2, 3), (2, 8),   -- Light Lunch: Grilled Chicken Salad and Greek Salad
(3, 4), (3, 6),   -- Family Dinner: Spaghetti Bolognese and Mashed Potatoes
(4, 2),           -- Afternoon Snack: Chocolate Chip Cookies
(5, 1), (5, 7),   -- Sunday Brunch: Pancakes and Banana Bread
(6, 5);           -- Quick Dinner: Vegetable Stir Fry

INSERT INTO meal_plan (user_id, day) VALUES
(1, 'Mon'), (1, 'Wed'), (1, 'Fri'),
(2, 'Tues'), (2, 'Thurs'), (2, 'Sat'),
(3, 'Sun'),
(4, 'Mon'), (4, 'Wed'), (4, 'Fri'),
(5, 'Tues'), (5, 'Thurs'), (5, 'Sun');

INSERT INTO meal_plan_meals (meal_plan_id, meal_id) VALUES
(1, 2), (2, 3), (3, 6),
(4, 1), (5, 3), (6, 4),
(7, 5),
(8, 2), (9, 3), (10, 6),
(11, 1), (12, 3), (13, 5);