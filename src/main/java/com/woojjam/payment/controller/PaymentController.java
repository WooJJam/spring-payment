package com.woojjam.payment.controller;


import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.woojjam.payment.client.PaymentApiClient;
import com.woojjam.payment.sevice.PaymentClientService;
import com.woojjam.payment.sevice.PaymentReadService;
import com.woojjam.payment.dto.PaymentReq;
import com.woojjam.payment.usecase.PaymentUseCase;
import com.woojjam.payment.dto.PaymentWebhookEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

	@Value("${payment.nice.store.id}")
	private String storeId;

	@Value("${payment.channel.key}")
	private String channelKey;

	@Value("${payment.secret.key}")
	private String secretKey;

	// @Value("${payment.id}")
	// private String paymentId;

	private final RestTemplate restTemplate;
	private final PaymentApiClient paymentApiClient;
	private final PaymentReadService paymentReadService;
	private final PaymentClientService paymentClientService;
	private final PaymentUseCase paymentUseCase;


	@GetMapping
	public String renderPaymentsView(Model model) {
		String paymentId = "payment-"+ UUID.randomUUID();
		model.addAttribute("storeId", storeId);
		model.addAttribute("channelKey", channelKey);
		model.addAttribute("paymentId", paymentId);

		return "payment";
	}


	/**
	 * @date : 9/4/24
	 * @author : woojjam
	 * @description : 결제 완료 v1
	 * 결제 완료시 클라이언트로부터 요청을 받아 결제 금액을 확인함
	 * 하지만 와이파이나 인터넷에 문제가 발생할 경우 결제 처리를 완료할 수 없음
	 * 또한 악의적인 사용자가 결제 완료 request를 보낼 경우 검증할 방법이 없음
	 */
	@ResponseBody
	@PostMapping("/complete")
	public void compltePaymentsV1(@RequestBody PaymentReq payments){
		log.info("Payments complete: {}", payments.getPaymentId());
		log.info("Payments Complete: txId = {}", payments.getTxId());
	}

	@ResponseBody
	@GetMapping("/info")
	public Object getPaymentInfo(@RequestParam("paymentId") String paymentId) {
		// TODO: 결제 정보 조회 API 호출

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "PortOne "+secretKey);
		HttpEntity<String> request = new HttpEntity<>(headers);

		ResponseEntity<Object> exchange = restTemplate.exchange(
			"https://api.portone.io/payments/"+paymentId,
			HttpMethod.GET,
			request,
			Object.class
		);

		return exchange;
	}

	/**
	 * @date : 9/4/24
	 * @author : woojjam
	 * @description : 결제 완료 V2
	 * Web-hook을 이용하여 안전성을 향상시킴
	 * Port-one 으로 부터 요청을 받기 때문에 클라이언트에 의존하지 않아도 됨
	 * Web-hook을 사용할 경우 클라이언트는 Polling 하고 있지 않기 때문에 최종 결과를 클라이언트에게 전달해야함
	 * Todo: SSE를 통해 서버는 클라이언트에게 결제 알림을 발신하여야 합니다.
	 */
	@ResponseBody
	@PostMapping("/pay")
	public void pay(@RequestBody PaymentWebhookEvent paymentWebhookEvent) {
		String paymentId = paymentWebhookEvent.getData().getPaymentId();
		String type = paymentWebhookEvent.getType();
		log.debug(paymentWebhookEvent.toString());

		paymentUseCase.pay(paymentId, type);

    }

}
