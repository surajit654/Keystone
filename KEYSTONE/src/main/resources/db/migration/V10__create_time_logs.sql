CREATE TABLE time_logs (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    technician_id BIGINT NOT NULL,
    minutes INTEGER NOT NULL,
    note VARCHAR(1000),

    CONSTRAINT fk_time_log_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id),

    CONSTRAINT fk_time_log_technician
        FOREIGN KEY (technician_id)
        REFERENCES users(id)
);