package com.woojjam.payment.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woojjam.payment.dto.PaymentRes;
import com.woojjam.payment.dto.RefundRes;

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
			log.error("[PaymentApiClient - readPaymentComplete] 결제 정보 조회 통신 오류");
			throw new RuntimeException("PAYMENT_INFO_FETCH_ERROR");
		}
	}

	public RefundRes refundAllPrice(String secretKey, String paymentId) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "PortOne " + secretKey);
		Map<String, String> body = new ConcurrentHashMap<>();
		body.put("reason", "환불 처리");
		HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);


		try {
			String response = restTemplate.exchange(
				"https://api.portone.io/payments/" + paymentId + "/cancel",
				HttpMethod.POST,
				request,
				String.class
			).getBody();

			return objectMapper.readValue(response, RefundRes.class);

		} catch (HttpClientErrorException e) {
			String errorResponse = e.getResponseBodyAsString();
			handleErrorResponse(errorResponse);
			throw new RuntimeException("PAYMENT_INFO_FETCH_ERROR");

		} catch (JsonProcessingException e) {
			log.error("RefundRes로 Parse하는 과정에서 예외 발생: {}", e.getMessage());
			throw new RuntimeException("Failed to parse RefundRes", e);
		}
	}

	private void handleErrorResponse(String error) {

		try {
			JsonNode jsonNode = objectMapper.readTree(error);
			String errorType = jsonNode.path("type").asText();

			switch (errorType) {
				case "PaymentNotPaidError" ->
					throw new RuntimeException("PAYMENT_NOT_PAID");
				case "PaymentAlreadyCancelledError" ->
					throw new RuntimeException("PAYMENT_ALREADY_CANCELLED_ERROR");
				case "CancellableAmountConsistencyBrokenError" ->
					throw new RuntimeException("CANCELLABLE_AMOUNT_CONSISTENCY_BROKEN");
				case "CancelAmountExceedsCancellableAmountError" ->
					throw new RuntimeException("CANCEL_AMOUNT_EXCEEDS_CANCELLABLE_AMOUNT");
				case "InvalidRequestError" ->
					throw new RuntimeException("INVALID_REQUEST_ERROR");
				case "PaymentNotFoundError" ->
					throw new RuntimeException("PAYMENT_ITEM_NOT_FOUND");
				case "UnauthorizedError" ->
					throw new RuntimeException("UNAUTHORIZED_ERROR");
				case "ForbiddenError" ->
					throw new RuntimeException("FORBIDDEN_ERROR");
				default -> {
					log.warn("Unknown error type: {}", errorType);
					throw new RuntimeException("PAYMENT_INFO_FETCH_ERROR");
				}
			}
		} catch (JsonProcessingException e) {
			log.error("JsonNode로 parse하는 과정에서 예외 발생: {}", e.getMessage());
			throw new RuntimeException("Failed to parse JsonNode", e);
		}

	}
}
