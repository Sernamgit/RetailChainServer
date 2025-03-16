-- Создание таблицы Shop
CREATE TABLE shop (
    id SERIAL PRIMARY KEY,
    number BIGINT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL
);

-- Создание таблицы Cash
CREATE TABLE cash (
    id SERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL,
    number BIGINT NOT NULL,
    shop_number BIGINT NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_time TIMESTAMP,
    CONSTRAINT fk_cash_shop FOREIGN KEY (shop_number) REFERENCES shop(number)
);