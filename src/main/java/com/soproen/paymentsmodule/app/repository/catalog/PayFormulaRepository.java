package com.soproen.paymentsmodule.app.repository.catalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.enums.YesNoEnum;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;

@Repository
public interface PayFormulaRepository extends JpaRepository<PayFormula, Long>{

	List<PayFormula> findAllByPayProgramAndIsActive(PayProgram payProgram, YesNoEnum isActive);
}
