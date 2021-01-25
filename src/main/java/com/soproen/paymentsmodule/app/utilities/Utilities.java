package com.soproen.paymentsmodule.app.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soproen.paymentsdto.dto.Identifiable;

@Component
public class Utilities {

	@Autowired
	private ObjectMapper objectMapper;

	public String formatDate(Date tmpDate, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.format(tmpDate);
	}

	public Boolean isObjectIdentifiable(Identifiable identifiableObj) {
		return identifiableObj != null && identifiableObj.getId() != null;
	}

	public <T> T mapObject(Object obj, Class<T> mapTo) {
		return (T) objectMapper.convertValue(obj, mapTo);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public <T> T mapObject(Object obj, TypeReference mapTo) {
		return (T) objectMapper.convertValue(obj, mapTo);
	}
	
	public <T> T isObjectIdentifiableAndMap(Object obj, Class<T> mapTo) throws SerialException {
		
		if(!(obj instanceof Identifiable)) {
			throw new SerialException("Object is not Identifiable"); 
		}
		return isObjectIdentifiable((Identifiable)obj)?mapObject(obj, mapTo):null;
	}
	
	public Long isObjectIdentifiableAndReturnId(Identifiable identifiableObj) {
		return isObjectIdentifiable(identifiableObj)?identifiableObj.getId():null;
	}
	
}
