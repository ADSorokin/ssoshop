INSERT INTO users (first_name, last_name, email, password, created_at, updated_at, role, active) VALUES
-- Админы
('Admin', 'System', 'admin@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 'ADMIN', true),
('John', 'Admin', 'john.admin@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-02 10:15:00', '2024-01-02 10:15:00', 'ADMIN', true),

-- Активные пользователи
('Alice', 'Johnson', 'alice.j@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-03 12:30:00', '2024-01-03 12:30:00', 'USER', true),
('Bob', 'Smith', 'bob.smith@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-04 14:45:00', '2024-01-04 14:45:00', 'USER', true),
('Carol', 'Williams', 'carol.w@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-05 16:20:00', '2024-01-05 16:20:00', 'USER', true),
('David', 'Brown', 'david.b@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-06 09:10:00', '2024-01-06 09:10:00', 'USER', true),
('Emma', 'Davis', 'emma.d@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-07 11:25:00', '2024-01-07 11:25:00', 'USER', true),

-- Неактивные пользователи
('Frank', 'Wilson', 'frank.w@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-08 13:40:00', '2024-01-08 13:40:00', 'USER', false),
('Grace', 'Taylor', 'grace.t@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-09 15:55:00', '2024-01-09 15:55:00', 'USER', false),

-- Активные пользователи с разными датами регистрации
('Henry', 'Anderson', 'henry.a@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-10 17:30:00', '2024-01-10 17:30:00', 'USER', true),
('Isabel', 'Martinez', 'isabel.m@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-11 08:45:00', '2024-01-11 08:45:00', 'USER', true),
('James', 'Thomas', 'james.t@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-12 10:20:00', '2024-01-12 10:20:00', 'USER', true),
('Kelly', 'White', 'kelly.w@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-13 12:35:00', '2024-01-13 12:35:00', 'USER', true),
('Liam', 'Harris', 'liam.h@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-14 14:50:00', '2024-01-14 14:50:00', 'USER', true),

-- Пользователи с недавней регистрацией
('Mia', 'Clark', 'mia.c@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-15 16:15:00', '2024-01-15 16:15:00', 'USER', true),
('Noah', 'Lewis', 'noah.l@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-16 09:30:00', '2024-01-16 09:30:00', 'USER', true),
('Olivia', 'Lee', 'olivia.l@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-17 11:45:00', '2024-01-17 11:45:00', 'USER', true),
('Peter', 'Walker', 'peter.w@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-18 14:00:00', '2024-01-18 14:00:00', 'USER', true),
('Quinn', 'Hall', 'quinn.h@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-19 16:15:00', '2024-01-19 16:15:00', 'USER', true),

-- Дополнительные пользователи
('Rachel', 'Young', 'rachel.y@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-20 08:30:00', '2024-01-20 08:30:00', 'USER', true),
('Samuel', 'King', 'samuel.k@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-21 10:45:00', '2024-01-21 10:45:00', 'USER', true),
('Tara', 'Wright', 'tara.w@example.com', '\$2a\$10$xn3LI/AjqicFYZFruSwve.681477XaVNaUQbr1gioaWPn4t1KsnmG', '2024-01-22 13:00:00', '2024-01-22 13:00:00', 'USER', true);

