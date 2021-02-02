package com.soproen.paymentsmodule.app.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

import lombok.extern.slf4j.Slf4j;
import uk.gov.nationalarchives.csv.validator.api.java.CsvValidator;
import uk.gov.nationalarchives.csv.validator.api.java.FailMessage;
import uk.gov.nationalarchives.csv.validator.api.java.Substitution;
import uk.gov.nationalarchives.csv.validator.api.java.WarningMessage;

@Slf4j
@Component
public class CsvUtils {

	
	@Value("${app.path-generated-payments-files}")
	private String generatedFilePath;
	@Value("${app.app-resources-folder}")
	private String appResourcesFolder;
	
	
	public Optional<List<String>> validateCsvFile(String csvSchemaValidation, Path fileToValidate) throws IOException {

		String csvSchema = appResourcesFolder.concat(csvSchemaValidation);

		Boolean failFast = false;
		List<Substitution> pathSubstitutions = new ArrayList<Substitution>();

		List<FailMessage> messages = CsvValidator.validate(fileToValidate.toString(), csvSchema, failFast,
				pathSubstitutions, true, false);

		if (messages.isEmpty()) {
			return Optional.empty();
		} else {

			return Optional.of(messages.stream().filter(obj -> !(obj instanceof WarningMessage)).map(obj -> {
				return obj.getMessage();
			}).collect(Collectors.toList()));
		}
	}
	
	public String createCsvFile(PayTermFile payTermFile, List<Object[]> dataList, String[] headers) throws ServiceException {
		try {

			String path = generatedFilePath.concat(payTermFile.getName()).concat(".csv");
			BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
			@SuppressWarnings("resource")
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
			csvPrinter.printRecords(dataList);

			csvPrinter.flush();
			writer.close();
			return path;

		} catch (IOException e) {
			log.error("createCsvFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	public List<String[]> readConciliationCsvFile(String uploadedFilePath) throws ServiceException {

		try {
			File file = new File(uploadedFilePath);

			InputStream input;
			input = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(input));

			List<String[]> listData = br.lines().skip(1).map(obj -> {

				String[] array = obj.split(",");
				String hhCode = array[4];
				
				String amountTransferred = "";
				if (array.length > 14) {
					amountTransferred = array[14];
				}
				
				String whoReceivedTransfer = "";
				if (array.length > 15) {
					whoReceivedTransfer = array[15];
				}
				
				String transferDate = "";
				if (array.length > 16) {
					transferDate = array[16];
				}
				
				String observation = "";
				if (array.length > 17) {
					observation = array[17];
				}

				return new String[] { hhCode, amountTransferred, whoReceivedTransfer, transferDate, observation };
			}).collect(Collectors.toList());

			br.close();
			input.close();

			return listData;

		} catch (IOException e) {
			log.error("readCsvFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
}
