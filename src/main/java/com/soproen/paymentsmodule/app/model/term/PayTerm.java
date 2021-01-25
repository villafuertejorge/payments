package com.soproen.paymentsmodule.app.model.term;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soproen.paymentsmodule.app.enums.PayTermTypeEnum;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayFormula;
import com.soproen.paymentsmodule.app.model.catalog.PayProgram;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * The persistent class for the pay_term database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_term")
public class PayTerm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="compliance_term_id")
	private Long complianceTermId;

	@Column(name="created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	private String name;

	@Enumerated(EnumType.STRING)
	private PayTermTypeEnum type;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "pay_formula_by_terms", joinColumns = @JoinColumn(name = "term_id"), 
	inverseJoinColumns = @JoinColumn(name = "formula_id"), uniqueConstraints = {	
			@UniqueConstraint(columnNames = { "term_id", "formula_id" }) })
	private List<PayFormula> payFormulas;

	@ManyToOne
	@JoinColumn(name="district_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayDistrict payDistrict;

	@ManyToOne
	@JoinColumn(name="program_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayProgram payProgram;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","payTerm" } ,allowSetters = true)
	@JoinColumn(name = "term_id",nullable = false)
	private List<PayTermFile> payTermFiles;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonIgnoreProperties(value={ "hibernateLazyInitializer", "handler","payTerm" } ,allowSetters = true)
	@JoinColumn(name = "term_id",nullable = false)
	private List<PayTermStatus> payTermStatuses;

}