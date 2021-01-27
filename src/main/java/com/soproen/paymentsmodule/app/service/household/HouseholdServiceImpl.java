package com.soproen.paymentsmodule.app.service.household;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.enums.PayHouseholdClaimStatus;
import com.soproen.paymentsmodule.app.enums.PayHouseholdStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdClaimValue;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdIdAndCodeDTO;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdInformationForPaymentFileDTO;
import com.soproen.paymentsmodule.app.repository.household.PayHouseholdRepository;
import com.soproen.paymentsmodule.app.repository.payment.PayHouseholdClaimValueRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HouseholdServiceImpl implements HouseholdService {

	@Autowired
	private PayHouseholdRepository payHouseholdRepository;
	@Autowired
	private PayHouseholdClaimValueRepository payHouseholdClaimValueRepository;

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

	@Override
	@Transactional(readOnly = true)
	public PayHouseholdIdAndCodeDTO findPayHouseholdIdAndCodeDTO(Long payHouseholdId) {
		try {
			return payHouseholdRepository.findPayHouseholdIdAndCodeDTO(payHouseholdId);
		} catch (DataAccessException e) {
			log.error("findPayHouseholdIdAndCodeDTO = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayHouseholdInformationForPaymentFileDTO> findPayHouseholdInformationForPaymentFileDTO(Long transferInstitutionId,
			PayHouseholdStatusEnum householdStatus, Long programId) {
		try {
			return payHouseholdRepository.findPayHouseholdInformationForPaymentFileDTO(transferInstitutionId, householdStatus.name(), programId);
		} catch (DataAccessException e) {
			log.error("findPayHouseholdInformationForPaymentFileDTO = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayHouseholdInformationForPaymentFileDTO> findPayHouseholdInformationForPaymentFileDTO(Long transferInstitutionId, Long districtId,
			PayHouseholdStatusEnum householdStatus, Long programId) {
		try {
			return payHouseholdRepository.findPayHouseholdInformationForPaymentFileDTO(transferInstitutionId, districtId, householdStatus.name(),
					programId);
		} catch (DataAccessException e) {
			log.error("findPayHouseholdInformationForPaymentFileDTO = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PayProgram findHouseholdProgram(PayHousehold payHousehold) {
		try {
			return payHouseholdRepository.findHouseholdProgram(payHousehold);
		} catch (DataAccessException e) {
			log.error("findHouseholdProgram = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayTransferInstitution> findPayTransferInstitutionsByDistrict(PayDistrict payDistrict) {
		try {
			return payHouseholdRepository.findTransferInstitutionsByDistrict(payDistrict);
		} catch (DataAccessException e) {
			log.error("findPayTransferInstitutionsByDistrict = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayHouseholdClaimValue> findAllClaimsValuesByPayHouseholdAndStatus(PayHousehold payHousehold, PayHouseholdClaimStatus status) {
		try {
			return payHouseholdClaimValueRepository.findAllByPayHouseholdAndStatus(payHousehold, status);
		} catch (DataAccessException e) {
			log.error("findAllClaimsValuesByPayHouseholdAndStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void saveAllClaimsValues(List<PayHouseholdClaimValue> payHouseholdClaimValueList) {
		try {
			payHouseholdClaimValueRepository.saveAll(payHouseholdClaimValueList);
		} catch (DataAccessException e) {
			log.error("saveAllClaimsValues = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

}
