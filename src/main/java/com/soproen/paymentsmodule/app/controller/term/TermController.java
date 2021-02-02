package com.soproen.paymentsmodule.app.controller.term;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.soproen.paymentsdto.dto.BasicValidation;
import com.soproen.paymentsdto.dto.term.PayCreateTermDTO;
import com.soproen.paymentsdto.dto.term.PayTermDTO;
import com.soproen.paymentsdto.enums.PayTermTypeEnumDTO;
import com.soproen.paymentsmodule.app.controller.AbstractParentController;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.term.PayTerm;
import com.soproen.paymentsmodule.app.service.term.TermService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/term")
public class TermController extends AbstractParentController {

	@Autowired
	private TermService termService;

	//
	@PostMapping("/createNewTerm")
	public ResponseEntity<?> createNewTerm(@Validated(BasicValidation.class) @Valid @RequestBody PayCreateTermDTO payCreateTermDTO,
			BindingResult bindingResult) {
		try {

			if (bindingResult.hasErrors()) {

				return new ResponseEntity<Map<String, Object>>(super.responseError("Validation Error", bindingResult), HttpStatus.OK);
			}

			if (payCreateTermDTO.getTermType().equals(PayTermTypeEnumDTO.BY_DISTRICT)
					&& (payCreateTermDTO.getPayDistrict() == null || payCreateTermDTO.getPayDistrict().getId() == null)) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("District is required"), HttpStatus.OK);
			}

			if (!termService.isTermCanBeCreated(payCreateTermDTO)) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("Term cannot be created, there is an open term"), HttpStatus.OK);
			}

			termService.createTerm(payCreateTermDTO);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", null), HttpStatus.OK);

		} catch (ServiceException e) {
			log.error("createNewTerm = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveAllByProgram/{programId}")
	public ResponseEntity<?> retrieveAllByProgram(@PathVariable(name = "programId", required = true) Long programId) {
		try {
			List<PayTermDTO> payTermDTOList = utilities.mapObject(
					termService.retrieveAllPayTermsByProgram(PayProgram.builder().id(programId).build()), new TypeReference<List<PayTermDTO>>() {
					});
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", payTermDTOList), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveAllByProgram = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("retrieveAllByProgram Fail"), HttpStatus.OK);
		}
	}

	@PostMapping("/closeTerm/{termId}/{username}")
	public ResponseEntity<?> closePayTerm(@PathVariable(name = "termId", required = true) Long termId,
			@PathVariable(name = "username", required = true) String username) {
		try {

//			if (!termService.isTermCanBeClosed(termId)) {
//				return new ResponseEntity<Map<String, Object>>(super.responseError("Term cannot be closed, there are processes running"),
//						HttpStatus.OK);
//			}
//			termService.closeTerm(termId, username);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", null), HttpStatus.OK);

		} catch (ServiceException e) {
			log.error("closePayTerm = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}

	@GetMapping("/retrieveById/{payTermId}")
	public ResponseEntity<?> retrieveById(@PathVariable(name = "payTermId", required = true) Long termId) {
		try {
			Optional<PayTerm> optTerm = termService.findPayTermById(termId);

			if (!optTerm.isPresent()) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("Term not found"), HttpStatus.OK);
			}

			PayTermDTO payTermDTO = utilities.mapObject(optTerm.get(), PayTermDTO.class);

			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", payTermDTO), HttpStatus.OK);
		} catch (ServiceException e) {
			log.error("retrieveById = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError("retrieveById Fail"), HttpStatus.OK);
		}
	}
}
