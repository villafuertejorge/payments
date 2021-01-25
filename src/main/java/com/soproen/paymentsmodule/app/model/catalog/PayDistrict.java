package com.soproen.paymentsmodule.app.model.catalog;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the pay_district database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pay_district")
public class PayDistrict implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private String name;

}