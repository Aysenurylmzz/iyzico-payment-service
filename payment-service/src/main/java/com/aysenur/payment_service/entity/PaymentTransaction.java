package com.aysenur.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "iyzico_transaction_id", nullable = false)
    private String iyzicoTransactionId;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "paid_price", precision = 12, scale = 2)
    private BigDecimal paidPrice;

    @Column(
        name = "refunded_amount",
        precision = 12,
        scale = 2,
        nullable = false
    )
    @Builder.Default
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    private String status;
}
