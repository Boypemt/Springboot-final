INSERT INTO customers (id, username, password, phone, email)
VALUES (1, 'crazydave', 'sunflower', '0812345678', 'dave@pvz.com');

INSERT INTO addresses (id, country, city, district, sub_district, zipcode, is_default, customer_id)
VALUES (1, 'Thailand', 'Bangkok', 'Suburb Lane', '123 Suburb Lane', '10110', TRUE, 1);
