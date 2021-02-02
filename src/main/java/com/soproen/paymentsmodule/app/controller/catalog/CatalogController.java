package com.soproen.paymentsmodule.app.controller.catalog;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.soproen.paymentsdto.dto.catalog.CompTermDTO;
import com.soproen.paymentsdto.dto.catalog.PayDistrictDTO;
import com.soproen.paymentsdto.dto.catalog.PayFormulaDTO;
import com.soproen.paymentsdto.dto.catalog.PayProgramDTO;
import com.soproen.paymentsmodule.app.controller.AbstractParentController;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.service.catalog.CatalogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/catalogs")
public class CatalogController extends AbstractParentController {

	@Value("${app.end-point-compliance-module-retrieve-closed-terms}")
	private String endPointComplianceModuleRetrieveClosedTerms;
	@Autowired
	private CatalogService catalogService;
	@Autowired
	@Qualifier("restTemplateCompliance")
	private RestTemplate restTemplateCompliance;

	@GetMapping("/retrieveAllPrograms")
	public ResponseEntity<?> retrieveAllPrograms() {
		try {
			List<PayProgramDTO> programList = utilities.mapObject(catalogService.retrieveAllPrograms(), new TypeReference<List<PayProgramDTO>>() {
			});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", programList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllPrograms = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all programs"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllDistricts")
	public ResponseEntity<?> retrieveAllDistricts() {
		try {

			List<PayDistrictDTO> districtList = utilities.mapObject(catalogService.retrieveAllDistricts(), new TypeReference<List<PayDistrictDTO>>() {
			});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", districtList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllDistricts = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve all districts"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllDistricts/{idDistrict}")
	public ResponseEntity<?> retrieveComplianceTermsBygeographicalLocation(@PathVariable(name = "idDistrict", required = true) Long idDistrict) {
		try {

			List<PayDistrictDTO> districtList = utilities.mapObject(catalogService.retrieveAllDistricts(), new TypeReference<List<PayDistrictDTO>>() {
			});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", districtList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("/retrieveAllDistricts/idDistrict = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve districts by id "), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveClosedComplianceTerms/{termType}/{idDistrict}")
	public ResponseEntity<?> retrieveClosedComplianceTerms(@PathVariable(name = "termType", required = true) String termType,
			@PathVariable(name = "idDistrict", required = true) String idDistrict) {
		try {

			String url = endPointComplianceModuleRetrieveClosedTerms.replace("{TERM_TYPE}", termType).replace("{DISTRICT_ID}", idDistrict);
			ResponseEntity<CompTermDTO[]> response = restTemplateCompliance.getForEntity(url, CompTermDTO[].class);

			if (response.getStatusCode().equals(HttpStatus.OK)) {
				return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", Arrays.asList(response.getBody())), HttpStatus.OK);
			} else {
				return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve closed compliance terms"), HttpStatus.OK);
			}

		} catch (ServiceException e) {
			log.error("retrieveClosedComplianceTerms = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve closed compliance terms"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllActiveFormulaByProgram/{idPayProgram}")
	public ResponseEntity<?> retrieveAllActiveFormulaByProgram(@PathVariable(name = "idPayProgram", required = true) Long idPayProgram) {
		try {

			List<PayFormulaDTO> districtList = utilities.mapObject(
					catalogService.retrieveAllActiveFormulasByProgram(PayProgram.builder().id(idPayProgram).build()),
					new TypeReference<List<PayFormulaDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", districtList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("/retrieveAllActiveFormulaByProgram/idProgram = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("Failed - retrieve active formulas by program "), HttpStatus.OK);
		}
	}

	@GetMapping("/retrievePayProgramById/{programId}")
	public ResponseEntity<?> retrieveProgramById(@PathVariable(name = "programId", required = true) Long programId) {
		try {
			PayProgramDTO program = utilities.mapObject(catalogService.findPayProgramById(programId), PayProgramDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", program), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveProgramById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("retrieveProgramById Fail"), HttpStatus.OK);
		}
	}

	@GetMapping("/retrievePayDistrictById/{idPayDistrict}")
	public ResponseEntity<?> retrievePayDistrictById(@PathVariable(name = "idPayDistrict", required = true) Long idPayDistrict) {
		try {
			PayDistrictDTO district = utilities.mapObject(catalogService.findPayDistrictById(idPayDistrict), PayDistrictDTO.class);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", district), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrievePayDistrictById = {}", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("retrievePayDistrictById Fail"), HttpStatus.OK);
		}
	}
}
