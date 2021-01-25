package com.soproen.paymentsmodule.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BeansConfiguration {
	
	@Value("${app.url-compliance-module}")
	private String urlComplianceModule;
	
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper()
		  .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	@Bean
	public RestTemplate restTemplateCompliance(RestTemplateBuilder builder) {
		return builder.rootUri(urlComplianceModule).build();
	}
}
