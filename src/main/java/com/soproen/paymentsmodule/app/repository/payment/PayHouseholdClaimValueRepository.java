package com.soproen.paymentsmodule.app.repository.payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.enums.PayHouseholdClaimStatus;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdClaimValue;

@Repository 
public interface PayHouseholdClaimValueRepository extends JpaRepository<PayHouseholdClaimValue,Long>{

	List<PayHouseholdClaimValue> findAllByPayHouseholdAndStatus(PayHousehold payHousehold,PayHouseholdClaimStatus status);

}
