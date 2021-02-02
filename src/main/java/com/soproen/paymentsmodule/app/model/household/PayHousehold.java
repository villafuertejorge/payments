package com.soproen.paymentsmodule.app.model.household;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soproen.paymentsmodule.app.enums.PayHouseholdStatusEnum;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;
import com.soproen.paymentsmodule.app.model.catalog.PayTa;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;
import com.soproen.paymentsmodule.app.model.catalog.PayVillage;
import com.soproen.paymentsmodule.app.model.catalog.PayZone;
import com.soproen.paymentsmodule.app.model.payment.PayHouseholdPaymentsRegistry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_households database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_households")
public class PayHousehold implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="household_id")
	private Long householdId;

	@Column(name="account_number")
	private String accountNumber;

	@Column(name="alternative_receiver_code")
	private String alternativeReceiverCode;

	@Column(name="alternative_receiver_id")
	private Integer alternativeReceiverId;

	@Column(name="alternative_receiver_name")
	private String alternativeReceiverName;

	@Column(name="contact_number")
	private String contactNumber;

	@Column(name="external_receiver_code")
	private String externalReceiverCode;

	@Column(name="external_receiver_id")
	private Integer externalReceiverId;

	@Column(name="external_receiver_name")
	private String externalReceiverName;

	@Column(name="household_code")
	private String householdCode;

	@Column(name="household_status")
	@Enumerated(EnumType.STRING)
	private PayHouseholdStatusEnum householdStatus;

	@Column(name="payment_receiver_code")
	private String paymentReceiverCode;

	@Column(name="payment_receiver_id")
	private Integer paymentReceiverId;

	@Column(name="payment_receiver_name")
	private String paymentReceiverName;

	@OneToMany(fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","payHousehold" },allowSetters = true )
	@JoinColumn(name = "household_id")
	private List<PayHouseholdClaimValue> payHouseholdClaimValues;
	
	@OneToMany(fetch = FetchType.LAZY)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","payHousehold" },allowSetters = true )
	@JoinColumn(name = "household_id")
	private List<PayHouseholdPaymentsRegistry> payHouseholdPaymentsRegistries;
	
	@ManyToOne
	@JoinColumn(name="district_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayDistrict payDistrict;
	

	@ManyToOne
	@JoinColumn(name="program_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayProgram payProgram;

	@ManyToOne
	@JoinColumn(name="ta_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayTa payTa;

	@ManyToOne
	@JoinColumn(name="transfer_institution_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayTransferInstitution payTransferInstitution;

	@ManyToOne
	@JoinColumn(name="village_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayVillage payVillage;

	@ManyToOne
	@JoinColumn(name="zone_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayZone payZone;

}