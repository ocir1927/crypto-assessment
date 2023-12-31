CREATE TABLE crypto_metrics (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    ticker VARCHAR(10) NOT NULL,
    min_price DOUBLE PRECISION NOT NULL,
    max_price DOUBLE PRECISION NOT NULL,
    oldest_price DOUBLE PRECISION NOT NULL,
    newest_price DOUBLE PRECISION NOT NULL,
    normalized_range DOUBLE PRECISION NOT NULL,
    price_start_date TIMESTAMP,
    price_end_date TIMESTAMP
);