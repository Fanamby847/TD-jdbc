create type dish_type as enum ('STARTER', 'MAIN', 'DESSERT');


create table dish
(
    id        serial primary key,
    name      varchar(255),
    dish_type dish_type
);

create type ingredient_category as enum ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

create table ingredient
(
    id       serial primary key,
    name     varchar(255),
    price    numeric(10, 2),
    category ingredient_category,
    id_dish  int references dish (id)
);

alter table dish
    add column if not exists price numeric(10, 2);


alter table ingredient
    add column if not exists required_quantity numeric(10, 2);

CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        reference VARCHAR(100) NOT NULL UNIQUE,
                        creation_datetime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dish_order (
                            id SERIAL PRIMARY KEY,
                            id_order INTEGER NOT NULL,
                            id_dish INTEGER NOT NULL,
                            quantity INTEGER NOT NULL CHECK (quantity > 0),

                            CONSTRAINT fk_dish_order_order
                                FOREIGN KEY (id_order)
                                    REFERENCES orders(id),

                            CONSTRAINT fk_dish_order_dish
                                FOREIGN KEY (id_dish)
                                    REFERENCES dish(id),

                            CONSTRAINT unique_order_dish
                                UNIQUE (id_order, id_dish)
);

CREATE TABLE ingredient_stock (
    id SERIAL PRIMARY KEY ,
    ingredient_id INTEGER NOT NULL ,
    quantity DOUBLE PRECISION NOT NULL ,
    mouvement_type VARCHAR(50) not null ,
    created_at timestamp not null default current_timestamp,
    constraint fk_stock_ingredient
                              foreign key (ingredient_id)
                              references ingredient(id)
);

alter table orders
add column type VARCHAR(50),
add column status VARCHAR(50);