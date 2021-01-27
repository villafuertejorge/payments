package com.soproen.paymentsmodule.app.service.household;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.repository.household.PayHouseholdRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayHouseholdServiceImpl implements PayHouseholdService {

	@Autowired
	private PayHouseholdRepository payHouseholdRepository;
	
	@Override
	@Transactional(readOnly = true)
	public Optional<PayHousehold> findPayHouseholdById(Long payHouseholdId) {
		try {
			return payHouseholdRepository.findById(payHouseholdId);
		} catch (DataAccessException e) {
			log.error("findPayHouseholdById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
}
