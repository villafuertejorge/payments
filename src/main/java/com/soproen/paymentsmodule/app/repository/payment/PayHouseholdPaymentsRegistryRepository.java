package com.soproen.paymentsmodule.app.repository.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.payment.PayHouseholdPaymentsRegistry;

@Repository 
public interface PayHouseholdPaymentsRegistryRepository extends JpaRepository<PayHouseholdPaymentsRegistry,Long>{

	List<PayHouseholdPaymentsRegistry> findAllByPayHousehold(PayHousehold payHousehold);

}
