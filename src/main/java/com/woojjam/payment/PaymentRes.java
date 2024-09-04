package com.woojjam.payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentRes {

	private String id;

	private String requestedAt;

	@JsonProperty("orderName")
	private String orderName;

	private Amount amount;

}

@Getter
@Setter
@ToString
class Amount {
	int total;
}
