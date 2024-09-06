package com.woojjam.payment.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.woojjam.payment.domain.Payment;
import com.woojjam.payment.dto.PaymentRes;
import com.woojjam.payment.sevice.PaymentClientService;
import com.woojjam.payment.sevice.PaymentReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentUseCase {

	@Value("${port.one.secret.key}")
	private String secretKey;

	private final PaymentClientService paymentClientService;
	private final PaymentReadService paymentReadService;

	public void pay(String paymentId, String type) {

		if (type.equals("Transaction.Paid")) {

			log.info("결제를 완료합니다. paymentId = {}", paymentId);
			PaymentRes paymentRes = paymentClientService.readPaymentInfo(secretKey, paymentId);
			Payment payment = paymentReadService.readPayment(paymentId);

			if (paymentRes.getId().equals(paymentId) && payment.getPrice() == paymentRes.getAmount().total()) {
				log.info("결제가 성공적으로 완료되었습니다.");
				// TODO: 결제 성공시 해당 결제 내역을 저장한다.
			} else {
				// TODO
				//  [O] 결제건에 대하여 환불을 진행한다.
				//  [x] 결제 실패시 해당 결제 내역을 저장한다.
				log.info("올바른 결제 요청이 아닙니다. 환불을 시작합니다.");
				paymentClientService.refundPayment(secretKey, paymentId);
			}

		}

		if (type.equals("Transaction.Cancelled")) {
			log.info("환불이 완료되었습니다.");
		}
	}
}
