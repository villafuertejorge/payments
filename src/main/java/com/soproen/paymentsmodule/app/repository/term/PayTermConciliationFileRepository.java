package com.soproen.paymentsmodule.app.repository.term;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.enums.PayTermConciliationFileStatusEnum;
import com.soproen.paymentsmodule.app.model.term.PayTermConciliationFile;

@Repository
public interface PayTermConciliationFileRepository extends JpaRepository<PayTermConciliationFile, Long> {

	Optional<PayTermConciliationFile> findTopOneByStatus(PayTermConciliationFileStatusEnum status);

}
