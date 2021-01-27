package com.soproen.paymentsmodule.app.service.term;

import java.util.List;
import java.util.Optional;

import com.soproen.paymentsdto.dto.term.PayCreateTermDTO;
import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.term.PayTerm;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

public interface TermService {

	Boolean isTermCanBeCreated(PayCreateTermDTO payCreateTermDTO) throws ServiceException;

	PayTerm createTerm(PayCreateTermDTO payCreateTermDTO) throws ServiceException;

	Optional<PayTermFile> findOnePayTermFileWithCurrentStatus(PayTermFileStatusEnum status) throws ServiceException;

	void changePayTermFileStatus(PayTermFile payTermFileTmp, PayTermFileStatusEnum newStatus, String errorDescription)
			throws ServiceException;

	void updatePayTermFileInfo(PayTermFile payTermFileTmp, Integer numberOfRecords, String generatedFilePath)
			throws ServiceException;

	PayTermFile findPayTermFileById(Long id) throws ServiceException;

	List<PayTermFile> findPayTermFileWithCurrentStatus(PayTermFileStatusEnum status, Integer numberOfRecords) throws ServiceException;

}
