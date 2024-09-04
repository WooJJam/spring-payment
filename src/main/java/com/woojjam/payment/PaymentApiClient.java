package com.woojjam.payment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentApiClient {

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	public PaymentRes readPaymentComplete(String secretKey, String paymentId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "PortOne "+secretKey);
		HttpEntity<String> request = new HttpEntity<>(headers);

		String response = restTemplate.exchange(
			"https://api.portone.io/payments/" + paymentId,
			HttpMethod.GET,
			request,
			String.class
		).getBody();

		try {
			return objectMapper.readValue(response, PaymentRes.class);
		} catch (Exception e) {
			throw new RuntimeException("Payment read error", e);
		}
	}

	public String refundAllPrice(String secretKey, String paymentId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "PortOne "+secretKey);
		Map<String ,String> body = new ConcurrentHashMap<>();
		body.put("reason", "결제 금액이 일치하지 않음");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

		return restTemplate.exchange(
			"https://api.portone.io/payments/" + paymentId +"/cancel",
			HttpMethod.POST,
			request,
			String.class
		).getBody();

	}
}
