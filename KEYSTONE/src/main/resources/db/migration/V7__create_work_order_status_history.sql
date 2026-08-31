CREATE TABLE work_order_status_history (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    changed_at TIMESTAMP NOT NULL,
    changed_by BIGINT,

    CONSTRAINT fk_history_work_order
        FOREIGN KEY (work_order_id)
        REFERENCES work_orders(id),

    CONSTRAINT fk_history_changed_by
        FOREIGN KEY (changed_by)
        REFERENCES users(id)
);