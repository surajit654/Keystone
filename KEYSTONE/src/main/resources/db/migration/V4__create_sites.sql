CREATE TABLE sites (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    customer_id BIGINT NOT NULL,

    CONSTRAINT fk_site_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);