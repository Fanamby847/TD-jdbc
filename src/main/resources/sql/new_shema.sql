/*ALTER TABLE ingredient
DROP COLUMN id_dish;*/


ALTER TABLE dish
    ADD COLUMN selling_price DOUBLE PRECISION;


CREATE TABLE dish_ingredient (
                                 id SERIAL PRIMARY KEY,
                                 dish_id INTEGER NOT NULL,
                                 ingredient_id INTEGER NOT NULL,
                                 quantity DOUBLE PRECISION NOT NULL,
                                 unit VARCHAR(10) NOT NULL,

                                 CONSTRAINT fk_dish
                                     FOREIGN KEY (dish_id) REFERENCES dish(id),

                                 CONSTRAINT fk_ingredient
                                     FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);
