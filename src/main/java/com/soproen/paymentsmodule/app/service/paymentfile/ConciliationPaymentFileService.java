package com.soproen.paymentsmodule.app.service.paymentfile;

import java.util.List;
import java.util.Optional;

import com.soproen.paymentsmodule.app.enums.PayTermConciliationFileStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.term.PayTermConciliationFile;

public interface ConciliationPaymentFileService {

	void updatePayTermConciliationFileStatus(PayTermConciliationFile termConciliationFile, PayTermConciliationFileStatusEnum newStatus,
			String errorDescription) throws ServiceException;

	Optional<PayTermConciliationFile> retrievePendingConciliationFile() throws ServiceException;

	List<String[]> validateFile(PayTermConciliationFile payTermConciliationFile) throws ServiceException;

	void saveConciliationInfo(PayTermConciliationFile payTermConciliationFile, List<String[]> listData) throws ServiceException;

}
