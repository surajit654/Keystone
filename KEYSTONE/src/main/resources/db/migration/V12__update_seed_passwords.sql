UPDATE users
SET password = '$2a$10$IbrxHUoqOLMx14z86kAQe.EYuf5l4fO1SitHdJ/tyV.zjpL5shcP.'
WHERE email IN (
    'dispatcher@keystone.com',
    'technician@keystone.com',
    'manager@keystone.com',
    'customer@keystone.com'
);