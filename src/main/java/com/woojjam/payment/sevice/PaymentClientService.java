package com.woojjam.payment.sevice;

import org.springframework.stereotype.Service;

import com.woojjam.payment.client.PaymentApiClient;
import com.woojjam.payment.dto.PaymentRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentClientService {

	private final PaymentApiClient paymentApiClient;

	public PaymentRes readPaymentInfo(String secretKey, String paymentId) {
		return paymentApiClient.readPaymentComplete(secretKey, paymentId);
	}

	public void refundPayment(String secretKey, String paymentId) {
		paymentApiClient.refundAllPrice(secretKey, paymentId);
	}

}