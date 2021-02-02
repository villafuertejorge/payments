package com.soproen.paymentsmodule.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;
import com.soproen.paymentsmodule.app.service.term.TermService;
import com.soproen.paymentsmodule.app.utilities.CsvUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/payments")
public class PaymentsController extends AbstractParentController {

	
	@Value("${app.path-uploaded-conciliation-payments-files}")
	private String uploadedConciliationFilesPath;
	@Value("${app.file-name-csv-schema-validation}")
	private String fileNameCsvSchemaValidation;
	@Autowired
	private TermService termService;
	@Autowired
	private CsvUtils csvUtils;

	// http://localhost:8280/payments/downloadPaymentFile/367
	@RequestMapping(path = "/downloadPaymentFile/{termFileId}", method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadPaymentFile(@PathVariable(name = "termFileId") Long termFileId) {

		try {

			PayTermFile termFile = termService.findPayTermFileById(termFileId);
			File file = new File(termFile.getGeneratedFilePath());

			String filename = String.format("attachment; filename=%s.%s", termFile.getName(), "csv");
			HttpHeaders header = new HttpHeaders();
			header.add(HttpHeaders.CONTENT_DISPOSITION, filename);
			header.add("Cache-Control", "no-cache, no-store, must-revalidate");
			header.add("Pragma", "no-cache");
			header.add("Expires", "0");

			Path path = Paths.get(file.getAbsolutePath());
			ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

			return ResponseEntity.ok().headers(header).contentLength(file.length()).contentType(MediaType.parseMediaType("application/octet-stream"))
					.body(resource);

		} catch (ServiceException | IOException e) {
			log.error("downloadPaymentFile = {} ", e.getMessage());
			return ResponseEntity.notFound().build();
		}
	}
	
	
	@PostMapping("/uploadConciliationFile")
	public ResponseEntity<?> uploadConciliationFile(@RequestParam("file") MultipartFile file, @RequestParam("payTermFileId") Long payTermFileId,
			@RequestParam("username") String username, @RequestParam("fileName") String fileName) {
		try {

			if (!termService.isCanConciliateFile(payTermFileId)) {
				return new ResponseEntity<Map<String, Object>>(
						super.responseError("Previous resolution: The process cannot be executed because another file is " + 
								"currently in process. Please wait for the previous process to finish before uploading again."),
						HttpStatus.OK);
			}
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));

			// validate fileName
			if (!fileName.equals(termService.findPayTermFileById(payTermFileId).getName())) {
				return new ResponseEntity<Map<String, Object>>(super.responseError(
						"File name error: The file name is not equal to the generated file name, please adjust this information to continue."),
						HttpStatus.OK);
			}

			Path tempFile = Files.createTempFile("temp-", ".csv");
			Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

			Optional<List<String>> optValidation = csvUtils.validateCsvFile(fileNameCsvSchemaValidation, tempFile);
			if (optValidation.isPresent()) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("Validation Error", optValidation.get()), HttpStatus.OK);
			}

			termService.createNewConciliationFile(file, payTermFileId, username);

			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", null), HttpStatus.OK);
		} catch (ServiceException | IOException e) {
			log.error("uploadConciliationFile = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}

}
