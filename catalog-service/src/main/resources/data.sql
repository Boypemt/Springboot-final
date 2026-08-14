-- Initial Data for catalog-service (Plants vs Zombies Shop)

-- 1. Classes (3 classes)
INSERT INTO classes (id, classname, description) VALUES (1, 'Attack', 'Plants that deal damage to zombies');
INSERT INTO classes (id, classname, description) VALUES (2, 'Sun Producer', 'Plants that produce sun resources');
INSERT INTO classes (id, classname, description) VALUES (3, 'Defense', 'Plants that block or stall zombies');

-- 2. Environments (3 environments)
INSERT INTO environments (id, envname, description) VALUES (1, 'Day', 'Daytime lawn environment');
INSERT INTO environments (id, envname, description) VALUES (2, 'Night', 'Nighttime lawn environment with mushrooms');
INSERT INTO environments (id, envname, description) VALUES (3, 'Pool', 'Pool lawn environment with water lanes');

-- 3. Plants (8 real PvZ plants)
INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (1, 'Peashooter', 1, 1, 'Shoots peas at oncoming zombies', 300, 20, 100, 'Normal');

INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (2, 'Sunflower', 2, 1, 'Provides additional sun to plant more plants', 300, 0, 50, 'Slow');

INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (3, 'Wall-nut', 3, 1, 'Blocks zombies and protects other plants', 4000, 0, 50, 'None');

INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (4, 'Cherry Bomb', 1, 1, 'Explodes all zombies in an area', 300, 1800, 150, 'Very Fast');

INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (5, 'Snow Pea', 1, 1, 'Shoots frozen peas that damage and slow zombies', 300, 20, 175, 'Normal');

INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (6, 'Chomper', 1, 1, 'Devours a zombie whole but is vulnerable while chewing', 300, 1000, 150, 'Very Slow');

INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (7, 'Repeater', 1, 1, 'Shoots two peas at a time', 300, 40, 200, 'Normal');

INSERT INTO plants (id, name, class_id, environment_id, description, hp, dmg, sun_cost, action_speed) 
VALUES (8, 'Potato Mine', 3, 1, 'Packs a powerful punch but needs time to arm itself', 300, 1800, 25, 'Slow');

-- 4. Products (8 products matching plants)
INSERT INTO products (id, plant_id, price, stock) VALUES (1, 1, 100.00, 25);
INSERT INTO products (id, plant_id, price, stock) VALUES (2, 2, 50.00, 50);
INSERT INTO products (id, plant_id, price, stock) VALUES (3, 3, 50.00, 40);
INSERT INTO products (id, plant_id, price, stock) VALUES (4, 4, 150.00, 15);
INSERT INTO products (id, plant_id, price, stock) VALUES (5, 5, 175.00, 20);
INSERT INTO products (id, plant_id, price, stock) VALUES (6, 6, 150.00, 10);
INSERT INTO products (id, plant_id, price, stock) VALUES (7, 7, 200.00, 30);
INSERT INTO products (id, plant_id, price, stock) VALUES (8, 8, 25.00, 35);
