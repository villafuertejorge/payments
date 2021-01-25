package com.soproen.paymentsmodule.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.soproen.paymentsmodule.app.enums.PayHouseholdStatusEnum;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.repository.household.PayHouseholdRepository;

@SpringBootApplication
public class AppPaymentsApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(AppPaymentsApplication.class, args);
	}

	@Autowired
	PayHouseholdRepository PayHouseholdRepository;
	@Override
	public void run(String... args) throws Exception {
		
		try {
			
			System.out.println(PayHouseholdRepository.findHouseholdCode(PayHousehold.builder().householdId(32L).build()));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
