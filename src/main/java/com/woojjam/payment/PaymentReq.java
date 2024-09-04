package com.woojjam.payment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentReq {
	private String txId;
	private String paymentId;
}
