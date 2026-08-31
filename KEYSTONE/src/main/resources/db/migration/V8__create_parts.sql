CREATE TABLE parts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    part_number VARCHAR(100) UNIQUE,
    stock_quantity INTEGER NOT NULL,
    unit_cost NUMERIC(10, 2) NOT NULL
);