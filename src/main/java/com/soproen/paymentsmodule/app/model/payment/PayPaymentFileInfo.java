package com.soproen.paymentsmodule.app.model.payment;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_payment_file_info database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_payment_file_info")
public class PayPaymentFileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="account_number")
	private String accountNumber;

	@Column(name="alternative_receiver_code")
	private String alternativeReceiverCode;

	@Column(name="alternative_receiver_name")
	private String alternativeReceiverName;

	private Double amount;

	@Column(name="amount_transferred")
	private String amountTransferred;

	@Column(name="contact_number")
	private String contactNumber;

	@Column(name="district_name")
	private String districtName;

	@Column(name="external_receiver_code")
	private String externalReceiverCode;

	@Column(name="external_receiver_name")
	private String externalReceiverName;

	@Column(name="household_code")
	private String householdCode;

	private String observation;

	@Column(name="payment_receiver_code")
	private String paymentReceiverCode;

	@Column(name="payment_receiver_name")
	private String paymentReceiverName;

	@Column(name="ta_name")
	private String taName;

	@Temporal(TemporalType.DATE)
	@Column(name="transfer_date")
	private Date transferDate;

	@Column(name="transfer_received_by")
	private String transferReceivedBy;

	@Column(name="village_name")
	private String villageName;

	@Column(name="zone_name")
	private String zoneName;

	@Column(name="household_id")
	private Long payHouseholdId;

	@Column(name="term_file_id")
	private Long payTermFileId;

	
}