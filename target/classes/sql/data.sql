set search_path to public;

insert into dish (id, name, dish_type, price, selling_price)
values (1, 'Salade fraîche', 'STARTER', 1200.0, 2500.0),
       (2, 'Poulet grillé', 'MAIN', 3000.0, 6000.0),
       (3, 'Riz aux légumes', 'MAIN', 2000.0, 4500.0),
       (4, 'Gâteau au chocolat', 'DESSERT', 2500.0, 5000.0),
       (5, 'Salade de fruits', 'DESSERT', 1800.0, 3500.0);

insert into ingredient (id, name, category, price)
values (1, 'Laitue', 'VEGETABLE', 800.0),
       (2, 'Tomate', 'VEGETABLE', 600.0),
       (3, 'Poulet', 'ANIMAL', 4500.0),
       (4, 'Chocolat', 'OTHER', 3000.0),
       (5, 'Beurre', 'DAIRY', 2500.0),
       (6, 'Riz', 'OTHER', 1500.0),
       (7, 'Carotte', 'VEGETABLE', 500.0),
       (8, 'Pomme', 'VEGETABLE', 700.0),
       (9, 'Banane', 'VEGETABLE', 600.0),
       (10, 'Huile', 'DAIRY', 3000.0);

insert into dish_ingredient (dish_id, ingredient_id, quantity, unit)
values (1, 1, 1.0, 'piece'),
       (1, 2, 0.25, 'kg'),
       (2, 3, 0.5, 'kg'),
       (2, 10, 0.15, 'L'),
       (3, 6, 0.3, 'kg'),
       (3, 7, 0.2, 'kg'),
       (4, 4, 0.1, 'kg'),
       (4, 5, 0.05, 'kg'),
       (5, 8, 0.3, 'kg'),
       (5, 9, 0.2, 'kg');

insert into stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime)
values (1, 1, 5.0, 'IN', 'KG', '2024-01-05 08:00:00+00'),
       (2, 1, 0.2, 'OUT', 'KG', '2024-01-06 11:00:00+00'),
       (3, 2, 4.0, 'IN', 'KG', '2024-01-05 08:00:00+00'),
       (4, 2, 0.15, 'OUT', 'KG', '2024-01-06 11:00:00+00'),
       (5, 3, 10.0, 'IN', 'KG', '2024-01-04 09:00:00+00'),
       (6, 3, 1.0, 'OUT', 'KG', '2024-01-06 11:00:00+00'),
       (7, 4, 3.0, 'IN', 'KG', '2024-01-05 10:00:00+00'),
       (8, 4, 0.3, 'OUT', 'KG', '2024-01-06 11:00:00+00'),
       (9, 5, 2.5, 'IN', 'KG', '2024-01-05 10:00:00+00'),
       (10, 5, 0.2, 'OUT', 'KG', '2024-01-06 11:00:00+00');
