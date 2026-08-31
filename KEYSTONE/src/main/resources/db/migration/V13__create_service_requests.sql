CREATE TABLE service_requests (
    id BIGSERIAL PRIMARY KEY,

    customer_id BIGINT NOT NULL,

    title VARCHAR(255) NOT NULL,

    description VARCHAR(2000) NOT NULL,

    status VARCHAR(50) NOT NULL,

    priority VARCHAR(50) NOT NULL,

    assigned_technician_id BIGINT,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP NOT NULL
);