package com.soproen.paymentsmodule.app.model.catalog;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the pay_village database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pay_village")
public class PayVillage implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	private String name;

	@ManyToOne
	@JoinColumn(name = "ta_id")
	private PayTa payTa;
	
}