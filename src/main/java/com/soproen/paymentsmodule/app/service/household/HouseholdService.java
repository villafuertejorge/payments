package com.soproen.paymentsmodule.app.service.household;

import java.util.List;
import java.util.Optional;

import com.soproen.paymentsmodule.app.enums.PayHouseholdClaimStatus;
import com.soproen.paymentsmodule.app.enums.PayHouseholdStatusEnum;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdClaimValue;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdIdAndCodeDTO;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdInformationForPaymentFileDTO;

public interface HouseholdService {

	Optional<PayHousehold> findPayHouseholdById(Long payHouseholdId);

	PayHouseholdIdAndCodeDTO findPayHouseholdIdAndCodeDTO(Long payHouseholdId);

	List<PayHouseholdInformationForPaymentFileDTO> findPayHouseholdInformationForPaymentFileDTO(Long transferInstitutionId,
			PayHouseholdStatusEnum householdStatus, Long programId);

	List<PayHouseholdInformationForPaymentFileDTO> findPayHouseholdInformationForPaymentFileDTO(Long transferInstitutionId, Long districtId,
			PayHouseholdStatusEnum householdStatus, Long programId);

	PayProgram findHouseholdProgram(PayHousehold payHousehold);

	List<PayTransferInstitution> findPayTransferInstitutionsByDistrict(PayDistrict payDistrict);

	List<PayHouseholdClaimValue> findAllClaimsValuesByPayHouseholdAndStatus(PayHousehold payHousehold, PayHouseholdClaimStatus status);

	void saveAllClaimsValues(List<PayHouseholdClaimValue> payHouseholdClaimValueList);

}
