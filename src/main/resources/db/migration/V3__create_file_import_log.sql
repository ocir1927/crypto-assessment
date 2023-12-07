CREATE TABLE file_import_log (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    date_time TIMESTAMP NOT NULL,
    file_hash VARCHAR(256) NOT NULL,
    file_name VARCHAR(255) NOT NULL
);