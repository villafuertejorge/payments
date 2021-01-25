package com.soproen.paymentsmodule.app.controller.term;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soproen.paymentsdto.dto.BasicValidation;
import com.soproen.paymentsdto.dto.term.PayCreateTermDTO;
import com.soproen.paymentsdto.enums.PayTermTypeEnumDTO;
import com.soproen.paymentsmodule.app.controller.AbstractParentController;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
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
	public ResponseEntity<?> createNewTerm(@Validated(BasicValidation.class) @Valid  @RequestBody PayCreateTermDTO payCreateTermDTO,
			BindingResult bindingResult) {
		try {
			
			if (bindingResult.hasErrors()) {
				
				return new ResponseEntity<Map<String, Object>>(super.responseError("Validation Error", bindingResult),
						HttpStatus.OK);
			}
			
			if (payCreateTermDTO.getTermType().equals(PayTermTypeEnumDTO.BY_DISTRICT)
					&& (payCreateTermDTO.getPayDistrict() == null || payCreateTermDTO.getPayDistrict().getId() == null)) {
				return new ResponseEntity<Map<String, Object>>(super.responseError("District is required"),
						HttpStatus.OK);
			}
			
			if (!termService.isTermCanBeCreated(payCreateTermDTO)) {
				return new ResponseEntity<Map<String, Object>>(
						super.responseError("Term cannot be created, there is an open term"), HttpStatus.OK);
			}
			
			
			termService.createTerm(payCreateTermDTO);
			return new ResponseEntity<Map<String, Object>>(super.responseOK("OK", null), HttpStatus.OK);
			
		}  catch (ServiceException e) {
			log.error("createNewTerm = {} ", e.getMessage());
			return new ResponseEntity<Map<String, Object>>(super.responseError(e.getMessage()), HttpStatus.OK);
		}
	}
}
