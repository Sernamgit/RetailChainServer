-- Создание таблицы Shift
CREATE TABLE shift (
    id SERIAL PRIMARY KEY,
    shift_number BIGINT NOT NULL,
    shop_number BIGINT NOT NULL,
    cash_number BIGINT NOT NULL,
    create_date TIMESTAMP NOT NULL,
    close_date TIMESTAMP,
    total BIGINT
);

-- Создание таблицы Purchase
CREATE TABLE purchase (
    id SERIAL PRIMARY KEY,
    shift_id BIGINT NOT NULL,
    purchase_date TIMESTAMP NOT NULL,
    total BIGINT NOT NULL,
    CONSTRAINT fk_purchase_shift FOREIGN KEY (shift_id) REFERENCES shift(id)
);

-- Создание таблицы Position
CREATE TABLE position (
    id SERIAL PRIMARY KEY,
    purchase_id BIGINT NOT NULL,
    barcode VARCHAR(255) NOT NULL,
    article BIGINT NOT NULL,
    price BIGINT NOT NULL,
    position_name VARCHAR(255) NOT NULL,
    CONSTRAINT fk_position_purchase FOREIGN KEY (purchase_id) REFERENCES purchase(id)
);