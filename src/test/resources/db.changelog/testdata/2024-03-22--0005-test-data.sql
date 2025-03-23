-- Shop
INSERT INTO shop (number, name, address) VALUES
(1, 'Shop 1', 'Address 1'),
(2, 'Shop 2', 'Address 2');

-- Cash
INSERT INTO cash (status, number, shop_number, create_date, update_date) VALUES
('ACTIVE', 1, 1, '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
('DELETED', 2, 2, '2024-01-01 10:00:00', '2024-01-01 10:00:00');

-- Item
INSERT INTO items (article, name, create_date, update_time) VALUES
(1001, 'Item 1', '2024-01-01 10:00:00', '2024-01-01 10:00:00'),
(1002, 'Item 2', '2024-01-01 10:00:00', '2024-01-01 10:00:00');

-- Price
INSERT INTO price (article, price) VALUES
(1001, 100),
(1001, 101),
(1001, 103),
(1002, 200);

-- Barcode
INSERT INTO barcode (barcode, article) VALUES
('1001111111111', 1001),
('1001111111112', 1001),
('1002111111111', 1002);

-- Shift
INSERT INTO shift (shift_number, shop_number, cash_number, create_date, close_date, total) VALUES
(1, 1, 1, '2024-01-01 10:00:00','2024-01-01 20:00:00', 201),
(2, 2, 2, '2024-01-01 10:00:00','2024-01-01 20:00:00', 200);

-- Purchase
INSERT INTO purchase (shift_id, purchase_date, total) VALUES
(1, '2024-01-01 10:00:00', 201),
(2, '2024-01-01 10:00:00', 200);

-- Position
INSERT INTO position (purchase_id, barcode, article, price, position_name) VALUES
(1, '1001111111111', 1001, 100, 'Item 1'),
(1, '1001111111112', 1001, 101, 'Item 1'),
(2, '2222222222222', 1002, 200, 'Item 2');