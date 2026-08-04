CREATE TABLE payment_transaction (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    iyzico_transaction_id VARCHAR(64) NOT NULL,
    item_id VARCHAR(64),
    paid_price NUMERIC(12, 2),
    refunded_amount NUMERIC(12, 2) NOT NULL DEFAULT 0,
    status VARCHAR(24),

    CONSTRAINT fk_payment_transaction_payment
        FOREIGN KEY (payment_id)
        REFERENCES payments(id)
);
