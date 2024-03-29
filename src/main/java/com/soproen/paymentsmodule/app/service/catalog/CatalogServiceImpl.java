package com.soproen.paymentsmodule.app.service.catalog;

import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.enums.YesNoEnum;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;
import com.soproen.paymentsmodule.app.repository.catalog.PayDistrictRepository;
import com.soproen.paymentsmodule.app.repository.catalog.PayFormulaBaseRepository;
import com.soproen.paymentsmodule.app.repository.catalog.PayFormulaRepository;
import com.soproen.paymentsmodule.app.repository.catalog.PayFormulaVariableRepository;
import com.soproen.paymentsmodule.app.repository.catalog.PayProgramRepository;
import com.soproen.paymentsmodule.app.repository.catalog.PayTransferIntitutionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CatalogServiceImpl implements CatalogService {

	@Autowired
	private PayProgramRepository payProgramRepository;
	@Autowired
	private PayDistrictRepository payDistrictRepository;
	@Autowired
	private PayFormulaRepository payFormulaRepository;
	@Autowired
	private PayTransferIntitutionRepository payTransferIntitutionRepository;
	@Autowired
	private PayFormulaVariableRepository payFormulaVariableRepository;
	@Autowired
	private PayFormulaBaseRepository payFormulaBaseRepository;

	@Override
	@Transactional(readOnly = true)
	public List<PayProgram> retrieveAllPrograms() throws ServiceException {
		try {
			return payProgramRepository.findAll();
		} catch (DataAccessException e) {
			log.error("retrieveAllPrograms = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayDistrict> retrieveAllDistricts() throws ServiceException {
		try {
			return payDistrictRepository.findAll();
		} catch (DataAccessException e) {
			log.error("retrieveAllDistricts = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayFormula> retrieveAllActiveFormulasByProgram(PayProgram payProgram) throws ServiceException {
		try {
			return payFormulaRepository.findAllByPayProgramAndIsActive(payProgram, YesNoEnum.YES);
		} catch (DataAccessException e) {
			log.error("retrieveAllActiveFormulasByProgram = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PayDistrict findPayDistrictById(Long districtId) throws ServiceException {
		try {
			return payDistrictRepository.findById(districtId).get();
		} catch (DataAccessException e) {
			log.error("findPayDistrictById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PayFormula findPayFormulaById(Long formulaId) throws ServiceException {
		try {
			return payFormulaRepository.findById(formulaId).get();
		} catch (DataAccessException e) {
			log.error("findPayFormulaById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayTransferInstitution> findAllPayTransferInstitution() throws ServiceException {
		try {
			return payTransferIntitutionRepository.findAll();
		} catch (DataAccessException e) {
			log.error("findAllPayTransferInstitution = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PayProgram findPayProgramById(Long programId) throws ServiceException {
		try {
			return payProgramRepository.findById(programId).get();
		} catch (DataAccessException e) {
			log.error("findPayProgramById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

}
