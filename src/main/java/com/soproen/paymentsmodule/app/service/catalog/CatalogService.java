package com.soproen.paymentsmodule.app.service.catalog;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;

public interface CatalogService {

	List<PayProgram> retrieveAllPrograms() throws ServiceException;

	List<PayDistrict> retrieveAllDistricts() throws ServiceException;

	List<PayFormula> retrieveAllActiveFormulasByProgram(PayProgram payProgram) throws ServiceException;

	PayDistrict findPayDistrictById(Long districtId) throws ServiceException;

	PayFormula findPayFormulaById(Long formulaId) throws ServiceException;

	List<PayTransferInstitution> findAllPayTransferInstitution() throws ServiceException;

	PayProgram findPayProgramById(Long programId) throws ServiceException;

}
