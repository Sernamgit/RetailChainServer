-- Создание таблицы Item
CREATE TABLE item (
    article BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_time TIMESTAMP
);

-- Создание таблицы Price
CREATE TABLE price (
    id SERIAL PRIMARY KEY,
    article BIGINT NOT NULL,
    price BIGINT NOT NULL,
    CONSTRAINT fk_price_item FOREIGN KEY (article) REFERENCES item(article)
);

-- Создание таблицы Barcode
CREATE TABLE barcode (
    barcode VARCHAR(255) PRIMARY KEY,
    article BIGINT NOT NULL,
    CONSTRAINT fk_barcode_item FOREIGN KEY (article) REFERENCES item(article)
);