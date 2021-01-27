package com.soproen.paymentsmodule.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.soproen.paymentsmodule.app.model.term.PayTermFile;
import com.soproen.paymentsmodule.app.repository.payment.PayPaymentFileInfoRepository;
import com.soproen.paymentsmodule.app.service.paymentfile.GeneratePaymentFileService;
import com.soproen.paymentsmodule.app.service.paymentfile.HandlerGeneratePaymentFileService;

@SpringBootTest
class AppPaymentsApplicationTests {

	
	@Autowired
	private HandlerGeneratePaymentFileService handlerGeneratePaymentFileService;
	@Autowired
	private GeneratePaymentFileService generatePaymentFileService;
	@Autowired
	private PayPaymentFileInfoRepository payPaymentFileInfoRepository;
	
	@Test()
	void contextLoads() {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	

}
