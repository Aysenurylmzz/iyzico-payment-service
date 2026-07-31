package com.aysenur.payment_service.repository;

import java.util.Optional;
import com.aysenur.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByToken(String token);




}
