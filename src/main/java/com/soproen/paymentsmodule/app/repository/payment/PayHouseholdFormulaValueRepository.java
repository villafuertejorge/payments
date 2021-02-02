package com.soproen.paymentsmodule.app.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.model.payment.PayHouseholdFormulaValue;

@Repository 
public interface PayHouseholdFormulaValueRepository extends JpaRepository<PayHouseholdFormulaValue, Long>{

}
