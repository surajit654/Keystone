CREATE TABLE part_usage (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_cost NUMERIC(10, 2) NOT NULL,

    CONSTRAINT fk_part_usage_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id),

    CONSTRAINT fk_part_usage_part
        FOREIGN KEY (part_id)
        REFERENCES parts(id)
);