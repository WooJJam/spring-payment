package com.woojjam.payment;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentReader {

	private final PaymentRepository paymentRepository;


	public Payment findById(Long id) {
		return paymentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Payment not found."));
	}

}
