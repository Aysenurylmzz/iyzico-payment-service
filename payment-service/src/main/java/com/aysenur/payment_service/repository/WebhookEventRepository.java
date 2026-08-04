package com.aysenur.payment_service.repository;

import com.aysenur.payment_service.entity.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository
        extends JpaRepository<WebhookEvent, Long> {

    boolean existsByIyzicoRef(String iyzicoRef);
}
