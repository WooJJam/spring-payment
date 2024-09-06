package com.woojjam.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReq {
	private String txId;
	private String paymentId;
}
