package com.soproen.paymentsmodule.app.service.paymentfile;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsmodule.app.enums.PayTermConciliationFileStatusEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.term.PayTermConciliationFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class HandlerConciliationPaymentFileServiceImpl implements HandlerConciliationPaymentFileService {

	@Autowired
	private ConciliationPaymentFileService conciliationPaymentFileService;

	@Override
	@Transactional(rollbackFor = { ServiceException.class }, propagation = Propagation.NOT_SUPPORTED)
	public void conciliateFile() throws ServiceException {

		PayTermConciliationFile termConciliationFile = null;
		try {

			Optional<PayTermConciliationFile> opt;
			if (!(opt = conciliationPaymentFileService.retrievePendingConciliationFile()).isPresent()) {
				return;
			}
			termConciliationFile = opt.get();

			List<String[]> listData = conciliationPaymentFileService.validateFile(termConciliationFile);
			conciliationPaymentFileService.saveConciliationInfo(termConciliationFile, listData);

		} catch (ServiceException e) {
			if (termConciliationFile != null) {
				conciliationPaymentFileService.updatePayTermConciliationFileStatus(termConciliationFile, PayTermConciliationFileStatusEnum.ERROR,
						e.getMessage());
			}
			log.error("conciliateFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
}
