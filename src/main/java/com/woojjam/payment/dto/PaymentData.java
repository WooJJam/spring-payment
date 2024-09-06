package com.woojjam.payment.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PaymentData {

	private String transactionId;
	private String paymentId;
}
