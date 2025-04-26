#!/bin/bash

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'
echo -e "${GREEN}NibbleNotebook Database Setup${NC}"
echo -e "This script will set up the MySQL database for NibbleNotebook."
echo

read -s -p "Enter MySQL root password: " rootpass
echo

echo -e "\n${GREEN}Creating database and user...${NC}"
mysql -u root -p"$rootpass" <<EOF 2>/dev/null
CREATE DATABASE IF NOT EXISTS recipe_db;
CREATE USER IF NOT EXISTS 'nibbleuser'@'localhost' IDENTIFIED BY 'nibblepass';
GRANT ALL PRIVILEGES ON recipe_db.* TO 'nibbleuser'@'localhost';
FLUSH PRIVILEGES;
EOF

if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Could not create database or user.${NC}"
    echo "Please check your MySQL root password and try again."
    exit 1
fi

echo -e "${GREEN}Database and user created successfully.${NC}"

echo -e "\n${GREEN}Running queries.sql to set up tables and data...${NC}"
mysql -u nibbleuser -p'nibblepass' recipe_db < queries.sql

if [ $? -ne 0 ]; then
    echo -e "${RED}Error: Could not run queries.sql.${NC}"
    exit 1
fi

echo -e "${GREEN}Database setup completed successfully!${NC}"

# Update application.properties
echo -e "\n${GREEN}Updating application.properties...${NC}"
cat > src/main/resources/application.properties << EOF
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/recipe_db
spring.datasource.username=nibbleuser
spring.datasource.password=nibblepass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Disable Thymeleaf cache for development
spring.thymeleaf.cache=false

# Enable debug logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
EOF

echo -e "${GREEN}Setup complete! You can now run the application.${NC}" 