CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000),
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    sla_due_at TIMESTAMP NOT NULL,

    customer_id BIGINT NOT NULL,
    site_id BIGINT NOT NULL,
    assignee_id BIGINT,

    CONSTRAINT fk_work_order_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id),

    CONSTRAINT fk_work_order_site
        FOREIGN KEY (site_id)
        REFERENCES sites(id),

    CONSTRAINT fk_work_order_assignee
        FOREIGN KEY (assignee_id)
        REFERENCES users(id)
);