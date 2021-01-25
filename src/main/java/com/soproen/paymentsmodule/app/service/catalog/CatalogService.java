package com.soproen.paymentsmodule.app.service.catalog;

import java.util.List;

import org.hibernate.service.spi.ServiceException;

import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;

public interface CatalogService {

	List<PayProgram> retrieveAllPrograms() throws ServiceException;

	List<PayDistrict> retrieveAllDistricts() throws ServiceException;

	List<PayFormula> retrieveAllActiveFormulasByProgram(PayProgram payProgram) throws ServiceException;

}
