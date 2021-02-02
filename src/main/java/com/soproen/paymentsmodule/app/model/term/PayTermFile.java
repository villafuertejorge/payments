package com.soproen.paymentsmodule.app.model.term;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.soproen.paymentsmodule.app.model.catalog.PayDistrict;
import com.soproen.paymentsmodule.app.model.catalog.PayTransferInstitution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;


/**
 * The persistent class for the pay_term_files database table.
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="pay_term_files")
public class PayTermFile implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="generated_file_path")
	private String generatedFilePath;

	private String name;

	@Column(name="number_of_records")
	private Integer numberOfRecords;


	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "payTermFile" })
	@JoinColumn(name = "term_file_id",nullable = false)
	private List<PayTermFileStatus> payTermFileStatuses;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	@JoinColumn(name="district_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayDistrict payDistrict;

	@ManyToOne
	@JoinColumn(name = "term_id", insertable = false, updatable = false)
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "payTermFiles" })
	private PayTerm payTerm;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE })
	@JoinColumn(name="transfer_institution_id")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
	private PayTransferInstitution payTransferInstitution;
	
	
	@Singular
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler", "payTermFile" })
	@JoinColumn(name = "term_file_id",nullable = false)
	@OrderBy("id")
	private List<PayTermConciliationFile> payTermConciliationFiles;
	
	@Transient
	private Boolean isGeneratedFile;
	
}