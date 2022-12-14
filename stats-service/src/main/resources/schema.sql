CREATE TABLE IF NOT EXISTS stats
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    app       VARCHAR(512)                            NOT NULL,
    uri       VARCHAR(512)                            NOT NULL,
    ip        VARCHAR(255)                            NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE                NOT NULL DEFAULT now(),
    CONSTRAINT pk_id PRIMARY KEY (id)
);
