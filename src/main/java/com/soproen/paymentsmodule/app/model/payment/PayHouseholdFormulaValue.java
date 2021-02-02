package com.soproen.paymentsmodule.app.model.payment;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.soproen.paymentsmodule.app.enums.PayHouseholdFormulaValueStatusEnum;
import com.soproen.paymentsmodule.app.model.catalog.PayFormulaVariable;
import com.soproen.paymentsmodule.app.model.household.PayHousehold;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_household_formula_values database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_household_formula_values")
public class PayHouseholdFormulaValue implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name="household_id")
	private PayHousehold payhousehold;

	@Enumerated(EnumType.STRING)
	private PayHouseholdFormulaValueStatusEnum status;

	@ManyToOne
	@JoinColumn(name="formula_variable_id")
	private PayFormulaVariable payFormulaVariable;

	
}