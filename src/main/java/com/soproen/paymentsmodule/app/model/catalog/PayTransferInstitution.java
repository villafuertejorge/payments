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
 * The persistent class for the pay_transfer_institutions database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pay_transfer_institutions")
public class PayTransferInstitution implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	private String name;

}