package com.soproen.paymentsmodule.app.model.term;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.soproen.paymentsmodule.app.enums.PayTermStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The persistent class for the pay_term_status database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pay_term_status")
public class PayTermStatus implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "closed_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date closedAt;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	@Enumerated(EnumType.STRING)
	private PayTermStatusEnum status;

	@Column(name = "username_created_by")
	private String usernameCreatedBy;

}