package com.aysenur.payment_service.service;

import com.aysenur.payment_service.dto.PaymentRequest;
import com.aysenur.payment_service.dto.PaymentResponse;
import com.aysenur.payment_service.entity.Payment;
import com.aysenur.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = Payment.builder()
                .price(request.getPrice())
                .paidPrice(request.getPaidPrice())
                .currency(request.getCurrency())
                .status("PENDING")
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return new PaymentResponse(
                savedPayment.getId(),
                savedPayment.getPaymentId(),
                savedPayment.getConversationId(),
                savedPayment.getPrice(),
                savedPayment.getPaidPrice(),
                savedPayment.getCurrency(),
                savedPayment.getStatus(),
                savedPayment.getCreatedAt()
        );
    }
}
