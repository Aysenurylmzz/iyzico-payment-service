package com.aysenur.payment_service.repository;

import com.aysenur.payment_service.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository
        extends JpaRepository<PaymentTransaction, Long> {

}
