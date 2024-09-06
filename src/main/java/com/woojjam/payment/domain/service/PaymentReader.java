package com.woojjam.payment.domain.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.woojjam.payment.domain.Payment;
import com.woojjam.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentReader {

	private final PaymentRepository paymentRepository;


	public Optional<Payment> findById(String id) {
		return paymentRepository.findById(id);
	}

}
