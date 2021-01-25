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
 * The persistent class for the pay_ta database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_ta")
public class PayTa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	private String name;

	@ManyToOne
	@JoinColumn(name="district_id")
	private PayDistrict payDistrict;
	
}