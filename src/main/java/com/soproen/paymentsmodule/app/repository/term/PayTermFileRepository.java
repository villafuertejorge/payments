package com.soproen.paymentsmodule.app.repository.term;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.enums.PayTermFileStatusEnum;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

@Repository
public interface PayTermFileRepository extends JpaRepository<PayTermFile, Long>{

	Optional<PayTermFile> findTopOneByPayTermFileStatuses_statusAndPayTermFileStatuses_closedAtIsNull(PayTermFileStatusEnum status);
	
	List<PayTermFile> findByPayTermFileStatuses_statusAndPayTermFileStatuses_closedAtIsNull(PayTermFileStatusEnum status, Pageable pageable);

}
