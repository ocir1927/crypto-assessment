CREATE TABLE crypto_price (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    price_timestamp TIMESTAMP NOT NULL,
    ticker VARCHAR(10) NOT NULL,
    price DOUBLE PRECISION NOT NULL
);