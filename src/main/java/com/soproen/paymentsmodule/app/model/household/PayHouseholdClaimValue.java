package com.soproen.paymentsmodule.app.model.household;

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
import com.soproen.paymentsmodule.app.enums.PayHouseholdClaimStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_household_claim_values database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_household_claim_values")
public class PayHouseholdClaimValue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double amount;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Column(name="created_by")
	private String createdBy;

	@Enumerated(EnumType.STRING)
	private PayHouseholdClaimStatus status;
	
	@ManyToOne
	@JoinColumn(name = "household_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "payHouseholdClaimValues" })
	private PayHousehold payHousehold;


}