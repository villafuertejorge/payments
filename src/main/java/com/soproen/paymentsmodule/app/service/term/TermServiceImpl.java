package com.soproen.paymentsmodule.app.service.term;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soproen.paymentsdto.dto.term.PayCreateTermDTO;
import com.soproen.paymentsdto.enums.PayTermTypeEnumDTO;
import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermTypeEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;
import com.soproen.paymentsmodule.app.model.term.PayTerm;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;
import com.soproen.paymentsmodule.app.model.term.PayTermFileStatus;
import com.soproen.paymentsmodule.app.model.term.PayTermStatus;
import com.soproen.paymentsmodule.app.repository.catalog.PayDistrictRepository;
import com.soproen.paymentsmodule.app.repository.catalog.PayFormulaRepository;
import com.soproen.paymentsmodule.app.repository.catalog.PayTransferIntitutionRepository;
import com.soproen.paymentsmodule.app.repository.term.PayTermFileRepository;
import com.soproen.paymentsmodule.app.repository.term.PayTermRepository;
import com.soproen.paymentsmodule.app.utilities.Utilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TermServiceImpl implements TermService {

	@Autowired
	private PayTermRepository payTermRepository;
	@Autowired
	private PayTransferIntitutionRepository payTransferIntitutionRepository;
	@Autowired
	private PayDistrictRepository payDistrictRepository;
	@Autowired
	private PayFormulaRepository payFormulaRepository;
	@Autowired
	private PayTermFileRepository payTermFileRepository;
	@Autowired
	private Utilities utilities;

	@Override
	@Transactional(readOnly = true)
	public Boolean isTermCanBeCreated(PayCreateTermDTO payCreateTermDTO) throws ServiceException {
		try {

			List<PayTerm> termList = payTermRepository.findAll();

			Boolean existTermOpen;

			if (payCreateTermDTO.getTermType().equals(PayTermTypeEnumDTO.NATIONAL)) {
				existTermOpen = termList.stream().filter(termTmp -> {
					return termTmp.getPayTermStatuses().stream()
							.filter(obj -> obj.getClosedAt() == null && obj.getStatus().equals(PayTermStatusEnum.OPEN))
							.findAny().isPresent();
				}).findAny().isPresent();
			} else {

				// validate national term
				existTermOpen = (termList.stream().filter(termTmp -> {
					return termTmp.getType().equals(PayTermTypeEnum.NATIONAL) && termTmp.getPayTermStatuses().stream()
							.filter(obj -> obj.getClosedAt() == null && obj.getStatus().equals(PayTermStatusEnum.OPEN))
							.findAny().isPresent();
				}).findAny().isPresent()) ||
				// validate district term
						(termList.stream().filter(termTmp -> {
							return termTmp.getPayDistrict() != null
									&& termTmp.getPayDistrict().getId() == payCreateTermDTO.getPayDistrict().getId()
									&& termTmp.getPayTermStatuses().stream()
											.filter(obj -> obj.getClosedAt() == null
													&& obj.getStatus().equals(PayTermStatusEnum.OPEN))
											.findAny().isPresent();
						}).findAny().isPresent());
			}

			return !existTermOpen;

		} catch (DataAccessException e) {
			log.error("isTermCanBeCreated = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public PayTerm createTerm(PayCreateTermDTO payCreateTermDTO) throws ServiceException {
		try {

			Date currentDate = Calendar.getInstance().getTime();

			PayProgram payProgram = utilities.mapObject(payCreateTermDTO.getPayProgram(), PayProgram.class);
			Long complianceTermId = utilities.isObjectIdentifiableAndReturnId(payCreateTermDTO.getCompTermDTO());
			List<PayFormula> payFormulas = payCreateTermDTO.getPayFormulas().stream().map(obj -> {
				return payFormulaRepository.findById(obj.getId()).get();
			}).collect(Collectors.toList());

			PayTermTypeEnum type = PayTermTypeEnum.valueOf(payCreateTermDTO.getTermType().name());

			PayTerm newTerm = PayTerm.builder().complianceTermId(complianceTermId).createdAt(currentDate)
					.payFormulas(payFormulas).payProgram(payProgram).type(type)
					.payTermStatuses(
							Arrays.asList(PayTermStatus.builder().createdAt(currentDate).status(PayTermStatusEnum.OPEN)
									.usernameCreatedBy(payCreateTermDTO.getUsernameCreatedBy()).build()))
					.build();

			String termName = payCreateTermDTO.getName();

			// Pay term files
			List<PayTermFile> payTermFileList;
			if (type.equals(PayTermTypeEnum.NATIONAL)) {
				payTermFileList = payTransferIntitutionRepository.findAll().stream().map(tranferInstTmp -> {
					return createPayTermFile(tranferInstTmp, currentDate);
				}).collect(Collectors.toList());
				termName = termName.concat("_").concat(PayTermTypeEnum.NATIONAL.name());
			} else {
				PayDistrict payDistrictTmp = payDistrictRepository.findById(payCreateTermDTO.getPayDistrict().getId())
						.get();
				payTermFileList = payTransferIntitutionRepository.findAll().stream().map(tranferInstTmp -> {
					PayTermFile termFileTmp = createPayTermFile(tranferInstTmp, currentDate);
					termFileTmp.setPayDistrict(payDistrictTmp);
					return termFileTmp;
				}).collect(Collectors.toList());
				termName = termName.concat("_").concat(payDistrictTmp.getName());
				newTerm.setPayDistrict(payDistrictTmp);
			}

			// termName
			termName = termName.concat("_").concat(utilities.formatDate(currentDate, "yyyy_MM_dd_HH_mm"));

			newTerm.setPayTermFiles(payTermFileList);
			newTerm.setName(termName);

			return payTermRepository.save(newTerm);

		} catch (DataAccessException e) {
			log.error("createTerm = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	private PayTermFile createPayTermFile(PayTransferInstitution trasnferInstTmp, Date currentDate) {

		String fileName = String.format("%s_%s_%s", trasnferInstTmp.getName(), PayTermTypeEnum.NATIONAL.name(),
				utilities.formatDate(currentDate, "yyyy_MM_dd_HH_mm"));
		return PayTermFile.builder().name(fileName)
				.payTermFileStatuses(Arrays.asList(PayTermFileStatus.builder().createdAt(currentDate)
						.status(PayTermFileStatusEnum.PENDING).build()))
				.numberOfRecords(0).payTransferInstitution(trasnferInstTmp).build();
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<PayTermFile> findOnePayTermFileWithCurrentStatus(PayTermFileStatusEnum status) throws ServiceException {
		try {
			return payTermFileRepository.findTopOneByPayTermFileStatuses_statusAndPayTermFileStatuses_closedAtIsNull(status);
		} catch (DataAccessException e) {
			log.error("findOnePayTermFileWithCurrentStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	@Transactional
	public void changePayTermFileStatus(PayTermFile payTermFileTmp, PayTermFileStatusEnum newStatus, String errorDescription)
			throws ServiceException {
		try {
			PayTermFile payTermFile = payTermFileRepository.findById(payTermFileTmp.getId()).get();

			Date currentDate = Calendar.getInstance().getTime();
			payTermFile.getPayTermFileStatuses().stream().forEach(payTermFileStatusTmp -> {
				if (payTermFileStatusTmp.getClosedAt() == null) {
					payTermFileStatusTmp.setClosedAt(currentDate);
				}
			});
			payTermFile.getPayTermFileStatuses().add(PayTermFileStatus.builder().createdAt(currentDate).status(newStatus)
					.errorDescription(errorDescription).build());
			payTermFileRepository.save(payTermFile);

		} catch (DataAccessException e) {
			log.error("changePayTermFileStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	@Override
	@Transactional
	public void updatePayTermFileInfo(PayTermFile payTermFileTmp, Integer numberOfRecords, String generatedFilePath)
			throws ServiceException {
		try {
			// update TermFile info
			PayTermFile payTermFile = payTermFileRepository.findById(payTermFileTmp.getId()).get();
			payTermFile.setNumberOfRecords(numberOfRecords);
			payTermFile.setGeneratedFilePath(generatedFilePath);
			payTermFileRepository.save(payTermFile);

		} catch (DataAccessException e) {
			log.error("updatePayTermFileInfo = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

}
