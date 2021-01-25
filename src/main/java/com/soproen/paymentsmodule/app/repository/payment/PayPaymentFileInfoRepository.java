package com.soproen.paymentsmodule.app.repository.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.model.payment.PayPaymentFileInfo;

@Repository 
public interface PayPaymentFileInfoRepository extends JpaRepository<PayPaymentFileInfo,Long> {

	List<PayPaymentFileInfo> findAllByPayTermFileId(Long id);

}
