package com.openfog.condo.dsign.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.openfog.condo.dsign.payment.model.Payment;
import com.openfog.condo.dsign.payment.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@RequestMapping(value = "/pay", method = RequestMethod.PUT)
	public Payment search(@RequestBody Payment payment) {
		try {
			return paymentService.pay(payment);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
