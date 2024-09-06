package com.woojjam.payment.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woojjam.payment.dto.PaymentRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

		try {
			return restTemplate.exchange(
				"https://api.portone.io/payments/" + paymentId + "/cancel",
				HttpMethod.POST,
				request,
				String.class
			).getBody();
		} catch (Exception e) {
			log.info("이미 환불된 결제");
			throw new RuntimeException("Payment refund error", e);
		}
	}
}
