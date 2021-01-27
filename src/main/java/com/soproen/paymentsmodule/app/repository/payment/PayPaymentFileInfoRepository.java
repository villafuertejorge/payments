package com.soproen.paymentsmodule.app.repository.payment;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.enums.PayPaymentFileInfoStatusEnum;
import com.soproen.paymentsmodule.app.model.payment.CalculateAmountResumeDTO;
import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;

@Repository
public interface PayPaymentFileInfoRepository extends JpaRepository<PayPaymentFileInfo, Long> {

	List<PayPaymentFileInfo> findAllByPayTermFileId(Long id);

	List<PayPaymentFileInfo> findByStatus(PayPaymentFileInfoStatusEnum status, Pageable pageable);

	@Query(value = "select pay_term_files.id as payTermFileId, \r\n" + "pay_payment_file_info.status as payPaymentFileInfoStatus, \r\n"
			+ "count(*) as numberOfRecords\r\n" + "from payments.pay_term_files pay_term_files\r\n"
			+ "join payments.pay_payment_file_info pay_payment_file_info\r\n" + "	on pay_payment_file_info.term_file_id = pay_term_files.id\r\n"
			+ "where pay_term_files.id =  :termFileId \r\n" + "group by pay_term_files.id , pay_payment_file_info.status", nativeQuery = true)
	List<CalculateAmountResumeDTO> findCalculateAmountResume(@Param("termFileId") Long termFileId);

}
