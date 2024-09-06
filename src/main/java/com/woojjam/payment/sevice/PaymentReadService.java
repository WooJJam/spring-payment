package com.woojjam.payment.sevice;

import org.springframework.stereotype.Service;

import com.woojjam.payment.domain.Payment;
import com.woojjam.payment.domain.service.PaymentReader;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentReadService {

	private final PaymentReader paymentReader;
	public Payment readPayment(String paymentId) {
		Payment payment = readAndThrow(paymentId);
		log.debug("find payment: {}", payment.toString());

		return payment;
	}

	private Payment readAndThrow(String paymentId) {
		return paymentReader.findById(paymentId).orElseThrow(() -> new RuntimeException("Payment not found"));
	}
}
