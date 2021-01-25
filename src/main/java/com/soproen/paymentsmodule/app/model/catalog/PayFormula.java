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

import com.soproen.paymentsmodule.app.enums.YesNoEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the pay_formula database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pay_formula")
public class PayFormula implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	@ManyToOne
	@JoinColumn(name="program_id")
	private PayProgram payProgram;
	@Column(name = "is_active")
	@Enumerated(EnumType.STRING)
	private YesNoEnum isActive;

}