package com.soproen.paymentsmodule.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import com.soproen.paymentsmodule.app.service.paymentfile.HandlerGeneratePaymentFileService;
import com.soproen.paymentsmodule.app.service.term.TermService;
import com.soproen.paymentsmodule.app.utilities.CsvUtils;

@SpringBootTest
class AppPaymentsApplicationTests {

	
	@Autowired
	private HandlerGeneratePaymentFileService handlerGeneratePaymentFileService;
	
	@Value("${app.end-point-retrieve-household-compliance-information}")
	private String endPointRetrieveHouseholdComplianceInformation;
	
	@Autowired
	@Qualifier("restTemplateCompliance")
	private RestTemplate restTemplateCompliance;
	
	@Value("${app.path-uploaded-conciliation-payments-files}")
	private String uploadedConciliationFilesPath;
	@Value("${app.file-name-csv-schema-validation}")
	private String fileNameCsvSchemaValidation;
	@Autowired
	private CsvUtils csvUtils;
	
	/*
	 * 
	 * java.lang.NullPointerException
	at com.soproen.paymentsdto.dto.HeaderDTO.isResponseOK(HeaderDTO.java:21)
	at com.soproen.paymentsmodule.app.service.payment.PaymentServiceImpl.retrieveHouseholdComplianceInformation(PaymentServiceImpl.java:371)
	 */
	
	//@Test()
	void contextLoads() {
		try {
			//handlerGeneratePaymentFileService.handlerGeneratePaymentInformation();
			 handlerGeneratePaymentFileService.handlerCalculatePaymentAmount();
			//handlerGeneratePaymentFileService.handlerVerifyCompleteCalculateAmountProcess();
			//handlerGeneratePaymentFileService.handlerGeneratePaymentFile();
			
			
			
//			String url = endPointRetrieveHouseholdComplianceInformation.replace("{TERM_ID}", "44").replace("{HOUSEHOLD_CODE}",
//					"HH_CODE_745");
//			ResponseEntity<ResponseCompRegisteredConciliationDTO> response = restTemplateCompliance.getForEntity(url,
//					ResponseCompRegisteredConciliationDTO.class);
//			System.out.println(response);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void validateCsvFile() throws IOException {
		
		
		File myFile = new File("C:\\Users\\JorgeVillafuerteGord\\Documents\\JorgeVillafuerte\\0_Personal\\1_desarrollo\\proyectosPersonales\\ProyAyala\\2_compliance\\tmpFolder\\generated\\TI_1_Distrct_4_2021_01_27_00_01.csv");
		InputStream targetStream = new FileInputStream(myFile);
		Path tempFile = Files.createTempFile("temp-", ".csv");
		Files.copy(targetStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
		Optional<List<String>> optValidation = csvUtils.validateCsvFile(fileNameCsvSchemaValidation, tempFile);
		optValidation.get().forEach(System.out::println);
		
	}
	
	
	

}
