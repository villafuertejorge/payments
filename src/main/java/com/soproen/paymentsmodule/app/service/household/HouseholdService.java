package com.soproen.paymentsmodule.app.service.household;

import java.util.Optional;

import com.soproen.paymentsmodule.app.model.household.PayHousehold;

public interface PayHouseholdService {

	Optional<PayHousehold> findPayHouseholdById(Long payHouseholdId);

}
