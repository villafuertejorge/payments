package com.soproen.paymentsmodule.app.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;

import com.soproen.paymentsmodule.app.utilities.Utilities;

public abstract class AbstractParentController {

	@Autowired
	protected Utilities utilities;
	
	
	protected Map<String, Object> responseOK(String message, Object data){
		Map<String, Object> mapRespuesta = new HashMap<>();
		mapRespuesta.put("code", "0");
		mapRespuesta.put("message", message);
		mapRespuesta.put("data", data);
		mapRespuesta.put("errorList", Collections.EMPTY_LIST);
		return mapRespuesta;
	}
	
	protected Map<String, Object> responseError(String message){
		Map<String, Object> mapRespuesta = new HashMap<>();
		mapRespuesta.put("code", "2");
		mapRespuesta.put("message", message);
		mapRespuesta.put("data", null);
		mapRespuesta.put("errorList", Collections.EMPTY_LIST);
		return mapRespuesta;
	}
	
	protected Map<String, Object> responseError(String message, List<String> errorList){
		Map<String, Object> mapRespuesta = new HashMap<>();
		mapRespuesta.put("code", "3");
		mapRespuesta.put("message", message);
		mapRespuesta.put("data", null);
		mapRespuesta.put("errorList", errorList);
		return mapRespuesta;
	}
	
	protected Map<String, Object> responseError(String message, 
			BindingResult bindingResult) {
		Map<String, Object> mapRespuesta = new HashMap<>();
		mapRespuesta.put("code", "4");
		mapRespuesta.put("message", message);
		mapRespuesta.put("data", null);
		mapRespuesta.put("errorList", bindingResult.getFieldErrors().stream()
				.map(a -> "Field: " + a.getField() + " - " + a.getDefaultMessage()).collect(Collectors.toList()));
		return mapRespuesta;
	}
	
	
	
}
