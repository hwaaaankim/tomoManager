package com.dev.TomoAdministration.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_BUYER_LOG")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyerLog {

	@JsonIgnore
	@Id
	@Column(name = "BUYER_LOG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long buyerLogId;

	@Column(name = "BUYER_LOG_USERNAME")
	private String buyerLogUsername;

	@Column(name = "BUYER_LOG_PRICE")
	private String buyerLogPrice;
	
	@Column(name="BUYER_LOG_LINK")
	private String buyerLogLink;

	@Column(name = "BUYER_LOG_DATE")
	private Date buyerLogDate;
	
	@Column(name="BUYER_LOG_BUYER_ID")
	private Long buyerLogBuyerId;
	
}
