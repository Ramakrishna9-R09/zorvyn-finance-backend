-- Seed Roles
INSERT INTO roles (id, name, description, created_at, updated_at) VALUES 
(1, 'VIEWER', 'Can only view dashboard data', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'ANALYST', 'Can view records and access insights', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'ADMIN', 'Full management access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Admin User (password: admin123)
INSERT INTO users (id, email, password, first_name, last_name, status, role_id, created_at, updated_at) VALUES 
(1, 'admin@zorvyn.com', '$2a$10$3IgTDph88TdN7OZ.79rVouW.vXTtzQtBE/qFzR2Rio25YLvNagH36', 'Admin', 'User', 'ACTIVE', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed Sample Financial Records
INSERT INTO financial_records (id, amount, type, category, transaction_date, description, notes, created_by, created_at, updated_at) VALUES 
(1, 5000.00, 'INCOME', 'SALARY', '2024-03-01', 'Monthly Salary', 'March 2024 salary payment', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1200.00, 'EXPENSE', 'RENT', '2024-03-02', 'Monthly Rent', 'Apartment rent', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 150.00, 'EXPENSE', 'GROCERIES', '2024-03-03', 'Grocery Shopping', 'Weekly groceries', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 2000.00, 'INCOME', 'FREELANCE', '2024-03-05', 'Freelance Project', 'Web development project', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 80.00, 'EXPENSE', 'UTILITIES', '2024-03-06', 'Electric Bill', 'Monthly electricity', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6, 300.00, 'EXPENSE', 'ENTERTAINMENT', '2024-03-07', 'Concert Tickets', 'Weekend concert', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

ALTER TABLE roles ALTER COLUMN id RESTART WITH 4;
ALTER TABLE users ALTER COLUMN id RESTART WITH 2;
ALTER TABLE financial_records ALTER COLUMN id RESTART WITH 7;
