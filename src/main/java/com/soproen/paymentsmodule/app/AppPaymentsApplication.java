package com.soproen.paymentsmodule.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
