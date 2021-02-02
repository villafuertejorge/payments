package com.soproen.paymentsmodule.app.service.term;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.soproen.paymentsdto.dto.term.PayCreateTermDTO;
import com.soproen.paymentsdto.enums.PayTermTypeEnumDTO;
import com.soproen.paymentsmodule.app.enums.PayTermConciliationFileStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermStatusEnum;
import com.soproen.paymentsmodule.app.enums.PayTermTypeEnum;
import com.soproen.paymentsmodule.app.exceptions.ServiceException;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;
import com.soproen.paymentsmodule.app.model.term.PayTerm;
import com.soproen.paymentsmodule.app.model.term.PayTermConciliationFile;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;
import com.soproen.paymentsmodule.app.model.term.PayTermFileStatus;
import com.soproen.paymentsmodule.app.model.term.PayTermStatus;
import com.soproen.paymentsmodule.app.repository.term.PayTermConciliationFileRepository;
import com.soproen.paymentsmodule.app.repository.term.PayTermFileRepository;
import com.soproen.paymentsmodule.app.repository.term.PayTermRepository;
import com.soproen.paymentsmodule.app.service.catalog.CatalogService;
import com.soproen.paymentsmodule.app.service.household.HouseholdService;
import com.soproen.paymentsmodule.app.utilities.Utilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TermServiceImpl implements TermService {

	@Value("${app.path-uploaded-conciliation-payments-files}")
	private String uploadedConciliationFilesPath;
	@Autowired
	private CatalogService catalogService;
	@Autowired
	private HouseholdService householdService;
	@Autowired
	private PayTermRepository payTermRepository;
	@Autowired
	private PayTermFileRepository payTermFileRepository;
	@Autowired
	private PayTermConciliationFileRepository payTermConciliationFileRepository;
	@Autowired
	private Utilities utilities;

	@Override
	@Transactional(readOnly = true)
	public Boolean isTermCanBeCreated(PayCreateTermDTO payCreateTermDTO) throws ServiceException {
		try {

			List<PayTerm> termList = payTermRepository.findAll();

			Boolean existsOpenTerm;

			if (payCreateTermDTO.getTermType().equals(PayTermTypeEnumDTO.NATIONAL)) {
				existsOpenTerm = termList.stream().filter(termTmp -> {
					return termTmp.getPayTermStatuses().stream()
							.filter(obj -> obj.getClosedAt() == null && obj.getStatus().equals(PayTermStatusEnum.OPEN)).findAny().isPresent();
				}).findAny().isPresent();
			} else {

				// validate national term
				existsOpenTerm = (termList.stream().filter(termTmp -> {
					return termTmp.getType().equals(PayTermTypeEnum.NATIONAL) && termTmp.getPayTermStatuses().stream()
							.filter(obj -> obj.getClosedAt() == null && obj.getStatus().equals(PayTermStatusEnum.OPEN)).findAny().isPresent();
				}).findAny().isPresent()) ||
				// validate district term
						(termList.stream().filter(termTmp -> {
							return termTmp.getPayDistrict() != null && termTmp.getPayDistrict().getId() == payCreateTermDTO.getPayDistrict().getId()
									&& termTmp.getPayTermStatuses().stream()
											.filter(obj -> obj.getClosedAt() == null && obj.getStatus().equals(PayTermStatusEnum.OPEN)).findAny()
											.isPresent();
						}).findAny().isPresent());
			}

			return !existsOpenTerm;

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
				return catalogService.findPayFormulaById(obj.getId());
			}).collect(Collectors.toList());

			PayTermTypeEnum type = PayTermTypeEnum.valueOf(payCreateTermDTO.getTermType().name());

			PayTerm newTerm = PayTerm.builder().complianceTermId(complianceTermId).createdAt(currentDate).payFormulas(payFormulas)
					.payProgram(payProgram).type(type).payTermStatuses(Arrays.asList(PayTermStatus.builder().createdAt(currentDate)
							.status(PayTermStatusEnum.OPEN).usernameCreatedBy(payCreateTermDTO.getUsernameCreatedBy()).build()))
					.build();

			String termName = payCreateTermDTO.getName();

			// Pay term files
			List<PayTermFile> payTermFileList;
			if (type.equals(PayTermTypeEnum.NATIONAL)) {

				payTermFileList = catalogService.findAllPayTransferInstitution().stream().map(tranferInstTmp -> {
					return createPayTermFile(tranferInstTmp, currentDate);
				}).collect(Collectors.toList());
				termName = termName.concat("_").concat(PayTermTypeEnum.NATIONAL.name());

			} else {
				PayDistrict payDistrictTmp = catalogService.findPayDistrictById(payCreateTermDTO.getPayDistrict().getId());

				payTermFileList = householdService.findPayTransferInstitutionsByDistrict(payDistrictTmp).stream().map(tranferInstTmp -> {

					PayTermFile termFileTmp = createPayTermFile(tranferInstTmp, payDistrictTmp, currentDate);
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
				.payTermFileStatuses(Arrays.asList(PayTermFileStatus.builder().createdAt(currentDate).status(PayTermFileStatusEnum.PENDING).build()))
				.numberOfRecords(0).payTransferInstitution(trasnferInstTmp).build();
	}

	private PayTermFile createPayTermFile(PayTransferInstitution trasnferInstTmp, PayDistrict payDistrictTmp, Date currentDate) {

		String fileName = String.format("%s_%s_%s", trasnferInstTmp.getName(), payDistrictTmp.getName(),
				utilities.formatDate(currentDate, "yyyy_MM_dd_HH_mm"));
		return PayTermFile.builder().name(fileName).payDistrict(payDistrictTmp)
				.payTermFileStatuses(Arrays.asList(PayTermFileStatus.builder().createdAt(currentDate).status(PayTermFileStatusEnum.PENDING).build()))
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
			payTermFile.getPayTermFileStatuses()
					.add(PayTermFileStatus.builder().createdAt(currentDate).status(newStatus).errorDescription(errorDescription).build());
			payTermFileRepository.save(payTermFile);

		} catch (DataAccessException e) {
			log.error("changePayTermFileStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void updatePayTermFileInfo(PayTermFile payTermFileTmp, Integer numberOfRecords, String generatedFilePath) throws ServiceException {
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

	@Override
	@Transactional(readOnly = true)
	public PayTermFile findPayTermFileById(Long id) throws ServiceException {
		try {
			return payTermFileRepository.findById(id).get();
		} catch (DataAccessException e) {
			log.error("findPayTermFileById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayTermFile> findPayTermFileWithCurrentStatus(PayTermFileStatusEnum status, Integer numberOfRecords) throws ServiceException {
		try {
			return payTermFileRepository.findByPayTermFileStatuses_statusAndPayTermFileStatuses_closedAtIsNull(status,
					PageRequest.of(0, numberOfRecords));
		} catch (DataAccessException e) {
			log.error("findPayTermFileWithCurrentStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean isCanConciliateFile(Long termFileId) throws ServiceException {
		try {

			PayTermFile termFile = this.findPayTermFileById(termFileId);

			Boolean existPendingFiles = termFile.getPayTermFileStatuses().stream().filter(obj -> {
				return obj.getClosedAt() == null && PayTermFileStatusEnum.isStatusInProcess(obj.getStatus());
			}).findAny().isPresent();

			Boolean existPendingConciliationFiles = termFile.getPayTermConciliationFiles().stream()
					.filter(obj -> obj.getStatus().equals(PayTermConciliationFileStatusEnum.PENDING)).findAny().isPresent();

			return !existPendingConciliationFiles && !existPendingFiles;

		} catch (DataAccessException e) {
			log.error("isCanConciliateFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional()
	public void createNewConciliationFile(MultipartFile file, Long termFileId, String username) throws ServiceException {
		try {

			Date currentDate = Calendar.getInstance().getTime();

			PayTermFile termFile = this.findPayTermFileById(termFileId);

			String filePath = uploadedConciliationFilesPath.concat(termFile.getName()).concat("_").concat(String.valueOf(currentDate.getTime()))
					.concat(".csv");
			Path path = Paths.get(filePath);
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			termFile.getPayTermConciliationFiles().add(PayTermConciliationFile.builder().createdAt(currentDate)
					.status(PayTermConciliationFileStatusEnum.PENDING).uploadedFilePath(filePath).username(username).build());

			payTermFileRepository.save(termFile);

		} catch (DataAccessException | IOException e) {
			log.error("createNewConciliationFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PayTerm> retrieveAllPayTermsByProgram(PayProgram payProgram) throws ServiceException {
		try {
			List<PayTerm> termList = payTermRepository.findAllByPayProgram(payProgram);
			prepatePayTermToReturn(termList);
			return termList;
		} catch (DataAccessException e) {
			log.error("retrieveAllPayTermsByProgram = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	private void prepatePayTermToReturn(List<PayTerm> payTermList) {

		payTermList.stream().forEach(termTmp -> {

			List<PayTermStatus> termStatusList = termTmp.getPayTermStatuses().stream().filter(termStatusTmp -> termStatusTmp.getClosedAt() == null)
					.collect(Collectors.toList());
			termTmp.setPayTermStatuses(termStatusList);

			termTmp.getPayTermFiles().stream().forEach(termFileTmp -> {

				List<PayTermFileStatus> termFileStatusList = termFileTmp.getPayTermFileStatuses().stream()
						.filter(status -> status.getClosedAt() == null).collect(Collectors.toList());
				termFileTmp.setPayTermFileStatuses(termFileStatusList);

				PayTermFileStatusEnum termFileStatusEnum = termFileStatusList.get(0).getStatus();
				termFileTmp.setIsGeneratedFile(!PayTermFileStatusEnum.isStatusInProcess(termFileStatusEnum)
						&& !termFileStatusEnum.equals(PayTermFileStatusEnum.EMPTY_FILE) && !termFileStatusEnum.equals(PayTermFileStatusEnum.ERROR));

			});

			List<PayTermFile> termFilesList = termTmp.getPayTermFiles();
			Collections.sort(termFilesList, new Comparator<PayTermFile>() {
				@Override
				public int compare(PayTermFile u1, PayTermFile u2) {
					return u1.getPayTermFileStatuses().get(0).getStatus().compareTo(u2.getPayTermFileStatuses().get(0).getStatus());
				}
			});
			termTmp.setPayTermFiles(termFilesList);

		});
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PayTerm> findPayTermById(Long payTermId) throws ServiceException {
		try {

			Optional<PayTerm> optTerm = payTermRepository.findById(payTermId);

			if (!optTerm.isPresent()) {
				return Optional.empty();
			}

			PayTerm termTmp = optTerm.get();
			termTmp.getPayTermFiles().stream().forEach(termFileTmp -> {

				List<PayTermFileStatus> termFileStatusList = termFileTmp.getPayTermFileStatuses().stream()
						.filter(status -> status.getClosedAt() == null).collect(Collectors.toList());
				termFileTmp.setPayTermFileStatuses(termFileStatusList);

				PayTermFileStatusEnum termFileStatusEnum = termFileStatusList.get(0).getStatus();
				termFileTmp.setIsGeneratedFile(!PayTermFileStatusEnum.isStatusInProcess(termFileStatusEnum)
						&& !termFileStatusEnum.equals(PayTermFileStatusEnum.EMPTY_FILE) && !termFileStatusEnum.equals(PayTermFileStatusEnum.ERROR));

				Long numberConciliation = termFileTmp.getPayTermConciliationFiles().stream().count();
				if (numberConciliation > 1) {
					termFileTmp.setPayTermConciliationFiles(
							termFileTmp.getPayTermConciliationFiles().stream().skip(numberConciliation - 1).collect(Collectors.toList()));
				}

			});

			List<PayTermStatus> termStatusList = termTmp.getPayTermStatuses().stream().filter(termStatusTmp -> termStatusTmp.getClosedAt() == null)
					.collect(Collectors.toList());
			termTmp.setPayTermStatuses(termStatusList);

			return Optional.of(termTmp);
		} catch (DataAccessException e) {
			log.error("findPayTermById = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<PayTermConciliationFile> findOnePayTermConciliationFileWithCurrentStatus(PayTermConciliationFileStatusEnum status)
			throws ServiceException {
		try {
			return payTermConciliationFileRepository.findTopOneByStatus(status);
		} catch (DataAccessException e) {
			log.error("findOnePayTermConciliationFileWithCurrentStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	@Transactional
	public void updatePayTermConciliationFileStatus(PayTermConciliationFile termConciliationFile, PayTermConciliationFileStatusEnum status,
			String errorDescription) throws ServiceException {
		try {

			PayTermFile termFile = payTermFileRepository.findById(termConciliationFile.getPayTermFile().getId()).get();
			termFile.getPayTermConciliationFiles().stream().forEach(obj -> {
				if (obj.getId() == termConciliationFile.getId()) {
					obj.setStatus(status);
					obj.setErrorDescription(errorDescription);
				}
			});
			payTermFileRepository.save(termFile);

		} catch (DataAccessException e) {
			log.error("updatePayTermConciliationFileStatus = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}

}
