package com.soproen.paymentsmodule.app.model.payment;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soproen.paymentsmodule.app.enums.PayHouseholdPaymentsRegistryStatusEnum;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;
import com.soproen.paymentsmodule.app.model.term.PayTermFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_household_payments_registry database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_household_payments_registry")
public class PayHouseholdPaymentsRegistry implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="current_amount")
	private Double currentAmount;
	
	@Column(name="arrears_amount")
	private Double arrearsAmount;
	
	@Column(name="total_amount")
	private Double totalAmount;

	@Column(name="closed_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date closedAt;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name="payment_details")
	private String paymentDetails;

	@Enumerated(EnumType.STRING)
	private PayHouseholdPaymentsRegistryStatusEnum status;

	@ManyToOne
	@JoinColumn(name = "household_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "payHouseholdPaymentsRegistries" })
	private PayHousehold payHousehold;
	
	@ManyToOne
	@JoinColumn(name = "term_file_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayTermFile payTermFile;


}