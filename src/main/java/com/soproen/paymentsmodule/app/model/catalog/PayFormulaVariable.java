package com.soproen.paymentsmodule.app.model.catalog;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_formula_variable database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_formula_variable")
public class PayFormulaVariable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	private String description;
	
	private String name;
	
	@Column(name="short_name")
	private String shortName;
	
	private String status;
	
	private String type;
}