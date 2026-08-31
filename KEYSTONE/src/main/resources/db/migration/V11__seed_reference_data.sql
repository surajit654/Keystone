-- Reference users
INSERT INTO users (email, password, role)
VALUES
    ('dispatcher@keystone.com', 'password', 'DISPATCHER'),
    ('technician@keystone.com', 'password', 'TECHNICIAN'),
    ('manager@keystone.com', 'password', 'MANAGER'),
    ('customer@keystone.com', 'password', 'CUSTOMER');

-- Reference customer
INSERT INTO customers (name, email, phone)
VALUES
    ('Meridian Facilities', 'contact@meridian.com', '9876543210');

-- Reference parts
INSERT INTO parts (name, part_number, stock_quantity, unit_cost)
VALUES
    ('Air Filter', 'AF-001', 50, 12.50),
    ('Copper Pipe', 'CP-001', 25, 35.00),
    ('Fuse', 'FU-001', 100, 5.00);