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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "TB_BUYER")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Buyer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="BUYER_ID")
	private Long buyerId;
	
	@Column(name="BUYER_USERNAME")
	private String buyerUsername;
	
	@Column(name="BUYER_EMAIL")
	private String buyerEmail;
	
	@Column(name="BUYER_BONUSRATE")
	private int buyerBonusRate;
	
	@Column(name="BUYER_JOINDATE")
	private Date buyerJoinDate;
	
	@Column(name="BUYER_MEMBER_ID")
	private Long buyerMemberId;
	
	@Column(name="BUYER_SIGN")
	private Boolean buyerSign;
	
	@OneToMany(
			fetch = FetchType.LAZY, 
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			mappedBy = "buyerLogBuyerId"
			)
	private List<BuyerLog> logs;
}
