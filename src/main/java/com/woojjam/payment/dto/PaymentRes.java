package com.woojjam.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class PaymentRes {

	private String id;
	private String requestedAt;
	private String orderName;
	private Amount amount;

	public record Amount (
		int total
	) {}
}
