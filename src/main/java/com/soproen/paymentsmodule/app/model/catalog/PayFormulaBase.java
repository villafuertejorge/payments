package com.soproen.paymentsmodule.app.model.catalog;

import java.io.Serializable;

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

import com.soproen.paymentsmodule.app.enums.PayFormulaBaseStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_formula_base database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_formula_base")
public class PayFormulaBase implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	private Long amount;

	@Column(name="short_name")
	private String shortName;

	@Enumerated(EnumType.STRING)
	private PayFormulaBaseStatusEnum status;

	@ManyToOne
	@JoinColumn(name="formula_variable_id")
	private PayFormulaVariable payFormulaVariable;
	
	@ManyToOne
	@JoinColumn(name="formula_id")
	private PayFormula payFormula;

}