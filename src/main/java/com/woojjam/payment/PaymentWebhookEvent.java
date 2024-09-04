package com.woojjam.payment;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PaymentWebhookEvent {
	private String type;
	private String timestamp;
	private PaymentData data;
}
