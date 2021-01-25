package com.soproen.paymentsmodule.app.repository.household;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.household.PayHouseholdInformationForPaymentFileDTO;

@Repository
public interface PayHouseholdRepository extends JpaRepository<PayHousehold, Long>{

	

	@Query(value = "select \r\n" + 
			"	p.household_id  as payHouseholdId\r\n" + 
			"	,district.\"name\" as districtName\r\n" + 
			"	,ta.\"name\" as taName\r\n" + 
			"	,village.\"name\" as villageName\r\n" + 
			"	,pay_zone.\"name\" as zoneName\r\n" + 
			"	,p.household_code as householdCode\r\n" + 
			"	,p.payment_receiver_name as paymentReceiverName\r\n" + 
			"	,p.payment_receiver_code as paymentReceiverCode\r\n" + 
			"	,p.alternative_receiver_name as alternativeReceiverName\r\n" + 
			"	,p.alternative_receiver_code as alternativeReceiverCode\r\n" + 
			"	,p.external_receiver_name as externalReceiverName\r\n" + 
			"	,p.external_receiver_code as externalReceiverCode\r\n" + 
			"	,0.0 as amount\r\n" + 
			"	,p.contact_number as contactNumber\r\n" + 
			"	,p.account_number as accountNumber\r\n" + 
			"from payments.pay_households p\r\n" + 
			"left join payments.pay_district district\r\n" + 
			"	on district.id = p.district_id\r\n" + 
			"left join payments.pay_ta ta\r\n" + 
			"	on ta.id = p.ta_id\r\n" + 
			"left join payments.pay_village village	\r\n" + 
			"	on village.id = p.village_id\r\n" + 
			"left join payments.pay_zone pay_zone\r\n" + 
			"	on pay_zone.id = p.zone_id\r\n" + 
			"where p.transfer_institution_id = :transferInstitutionId\r\n" + 
			"and p.household_status = :householdStatus\r\n"
			+ "and p.program_id = :programId\r\n", nativeQuery = true)
	List<PayHouseholdInformationForPaymentFileDTO> findPayHouseholdInformationForPaymentFileDTO(@Param("transferInstitutionId") Long transferInstitutionId,
		@Param("householdStatus") String householdStatus
		,@Param("programId") Long programId);
	
	@Query(value = "select \r\n" + 
			"	p.household_id  as payHouseholdId\r\n" + 
			"	,district.\"name\" as districtName\r\n" + 
			"	,ta.\"name\" as taName\r\n" + 
			"	,village.\"name\" as villageName\r\n" + 
			"	,pay_zone.\"name\" as zoneName\r\n" + 
			"	,p.household_code as householdCode\r\n" + 
			"	,p.payment_receiver_name as paymentReceiverName\r\n" + 
			"	,p.payment_receiver_code as paymentReceiverCode\r\n" + 
			"	,p.alternative_receiver_name as alternativeReceiverName\r\n" + 
			"	,p.alternative_receiver_code as alternativeReceiverCode\r\n" + 
			"	,p.external_receiver_name as externalReceiverName\r\n" + 
			"	,p.external_receiver_code as externalReceiverCode\r\n" + 
			"	,0.0 as amount\r\n" + 
			"	,p.contact_number as contactNumber\r\n" + 
			"	,p.account_number as accountNumber\r\n" + 
			"from payments.pay_households p\r\n" + 
			"left join payments.pay_district district\r\n" + 
			"	on district.id = p.district_id\r\n" + 
			"left join payments.pay_ta ta\r\n" + 
			"	on ta.id = p.ta_id\r\n" + 
			"left join payments.pay_village village	\r\n" + 
			"	on village.id = p.village_id\r\n" + 
			"left join payments.pay_zone pay_zone\r\n" + 
			"	on pay_zone.id = p.zone_id\r\n" + 
			"where p.transfer_institution_id = :transferInstitutionId\r\n"
			+ "and district.id = :districtId\r\n" + 
			" and p.household_status = :householdStatus\r\n" 
			+ "and p.program_id = :programId\r\n"  , nativeQuery = true)
	List<PayHouseholdInformationForPaymentFileDTO> findPayHouseholdInformationForPaymentFileDTO(@Param("transferInstitutionId") Long transferInstitutionId,
			@Param("districtId") Long districtId,
			@Param("householdStatus") String householdStatus
			,@Param("programId") Long programId);
	
	
	
	@Query("SELECT p.payProgram FROM PayHousehold p WHERE p = :payHousehold ")
	PayProgram findHouseholdProgram(@Param("payHousehold") PayHousehold payHousehold);

	@Query(value="SELECT p.householdCode FROM PayHousehold p WHERE p = :payHousehold ")
	String findHouseholdCode(@Param("payHousehold") PayHousehold payHousehold);
	

}
