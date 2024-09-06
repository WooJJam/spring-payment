package com.woojjam.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.woojjam.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
