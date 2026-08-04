package com.aysenur.payment_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhook_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    @Column(unique = true)
    private String iyzicoRef;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private Boolean signatureOk;

    private LocalDateTime processedAt;

    private LocalDateTime receivedAt;
}
