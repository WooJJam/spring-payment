package com.woojjam.payment.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class RefundRes {

	private CancellationDto cancellation;

	public record CancellationDto (
		String id,
		int totalAmount,
		String cancelledAt)
	{ }
}
