CREATE TABLE webhook_event (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(64),
    iyzico_ref VARCHAR(128) NOT NULL UNIQUE,
    payload TEXT NOT NULL,
    signature_ok BOOLEAN NOT NULL,
    processed_at TIMESTAMP,
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
