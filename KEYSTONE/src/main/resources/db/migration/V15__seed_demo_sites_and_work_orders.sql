-- Seed Sites for Meridian Facilities
INSERT INTO sites (name, address, city, customer_id)
VALUES
    ('Meridian Tower HQ', '100 Financial Way, Floor 14', 'New York', 1),
    ('Meridian Logistics Hub', '45 Industrial Pkwy, Bldg C', 'Newark', 1),
    ('Meridian Tech Center', '880 Innovation Blvd', 'Jersey City', 1);

-- Seed Additional Customers & Sites
INSERT INTO customers (name, email, phone)
VALUES
    ('Apex Global Logistics', 'ops@apexlogistics.com', '555-019-2834'),
    ('Omni Health Systems', 'facilities@omnihealth.org', '555-014-9921');

INSERT INTO sites (name, address, city, customer_id)
VALUES
    ('Apex Distribution Central', '12 Freight Lane', 'Philadelphia', 2),
    ('Omni Regional Medical Pavilion', '700 Health Sciences Dr', 'Boston', 3);

-- Seed Demo Work Orders across various states
INSERT INTO work_orders (code, title, description, priority, status, sla_due_at, customer_id, site_id, assignee_id)
VALUES
    ('WO-2026-0001', 'Main Server Room AC Unit Overheating', 'Server room temp reached 84F. Compressor needs inspection and filter replacement.', 'HIGH', 'IN_PROGRESS', NOW() + INTERVAL '2 hours', 1, 1, 2),
    ('WO-2026-0002', 'Lobby Automated Access Door Sensor Failure', 'Entry gate sensor intermittently failing during peak hours.', 'HIGH', 'NEW', NOW() + INTERVAL '3 hours', 1, 1, NULL),
    ('WO-2026-0003', 'Chilled Water Loop Valve Leakage', 'Minor coolant seepage detected in basement mechanical room near pump 3.', 'MEDIUM', 'ASSIGNED', NOW() + INTERVAL '18 hours', 1, 2, 2),
    ('WO-2026-0004', 'Backup Diesel Generator Transfer Switch Fault', 'Emergency generator transfer switch pending isolation breaker part delivery.', 'HIGH', 'ON_HOLD', NOW() + INTERVAL '4 hours', 2, 4, 2),
    ('WO-2026-0005', 'Executive Conference Smart Lighting Retrofit', 'Replaced all failing DALI dimmer modules and reprogrammed scene controllers.', 'MEDIUM', 'COMPLETED', NOW() - INTERVAL '1 hour', 1, 3, 2),
    ('WO-2026-0006', 'Rooftop Solar Array Bi-Annual Preventive Inspection', 'Completed full visual inspection, cleaned dust on panels, verified inverter efficiency.', 'LOW', 'CLOSED', NOW() - INTERVAL '1 day', 3, 5, 2);

-- Status History for Demo Work Orders
INSERT INTO work_order_status_history (work_order_id, status, changed_at, changed_by, note)
VALUES
    (1, 'NEW', NOW() - INTERVAL '3 hours', 1, 'Initial service ticket opened by dispatch'),
    (1, 'ASSIGNED', NOW() - INTERVAL '2 hours 30 minutes', 1, 'Assigned to technician@keystone.com'),
    (1, 'IN_PROGRESS', NOW() - INTERVAL '1 hour', 2, 'Technician arrived on site, commenced diagnosis'),
    
    (2, 'NEW', NOW() - INTERVAL '1 hour', 4, 'Ticket created via Customer Portal'),
    
    (3, 'NEW', NOW() - INTERVAL '4 hours', 1, 'Dispatched regular maintenance ticket'),
    (3, 'ASSIGNED', NOW() - INTERVAL '2 hours', 1, 'Assigned to technician@keystone.com'),
    
    (4, 'NEW', NOW() - INTERVAL '6 hours', 1, 'High priority generator fault ticket opened'),
    (4, 'ASSIGNED', NOW() - INTERVAL '5 hours', 1, 'Assigned to technician@keystone.com'),
    (4, 'IN_PROGRESS', NOW() - INTERVAL '4 hours', 2, 'Diagnosed circuit board fault'),
    (4, 'ON_HOLD', NOW() - INTERVAL '2 hours', 2, 'Awaiting specialized replacement breaker from supplier'),
    
    (5, 'NEW', NOW() - INTERVAL '8 hours', 1, 'Lighting maintenance requested'),
    (5, 'ASSIGNED', NOW() - INTERVAL '7 hours', 1, 'Assigned to technician@keystone.com'),
    (5, 'IN_PROGRESS', NOW() - INTERVAL '5 hours', 2, 'Replaced dimmer controllers'),
    (5, 'COMPLETED', NOW() - INTERVAL '1 hour', 2, 'All lighting scenes tested and working. Ready for manager close-out'),
    
    (6, 'NEW', NOW() - INTERVAL '2 days', 1, 'Scheduled PM routine'),
    (6, 'ASSIGNED', NOW() - INTERVAL '2 days', 1, 'Assigned to technician@keystone.com'),
    (6, 'IN_PROGRESS', NOW() - INTERVAL '30 hours', 2, 'Field inspection started'),
    (6, 'COMPLETED', NOW() - INTERVAL '26 hours', 2, 'Inspection finished, zero anomalies detected'),
    (6, 'CLOSED', NOW() - INTERVAL '24 hours', 3, 'Manager reviewed report and signed off');

-- Seed Part Usage
INSERT INTO part_usage (work_order_id, part_id, quantity, unit_cost)
VALUES
    (1, 1, 2, 12.50),
    (5, 3, 4, 5.00);

-- Seed Time Logs
INSERT INTO time_logs (work_order_id, technician_id, minutes, note)
VALUES
    (1, 2, 45, 'Diagnostic and initial intake check'),
    (5, 2, 90, 'Replaced dimmers and tested control bus'),
    (6, 2, 120, 'Comprehensive solar array inspection');
