package com.dev.TomoAdministration.model;

import java.io.Serializable;
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
@Table(name = "TB_MEMBER")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member implements Serializable{

	private static final long serialVersionUID = 2023L;

	@JsonIgnore
	@Id
	@Column(name = "MEMBER_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long memberId;

	@Column(name = "MEMBER_USERNAME", length = 500, unique = true)
	private String username;

	@Column(name = "MEMBER_PASSWORD", length = 500)
	private String password;
	
	@Column(name="MEMBER_NAME")
	private String memberName;

	@Column(name = "MEMBER_JOINDATE")
	private Date memberJoinDate;
	
	@Column(name="MEMBER_ACCOUNTNUMBER")
	private String memberAccountNumber;
	
	@Column(name="MEMBER_PARENT_USERNAME")
	private String memberParentUsername;
	
	@Column(name="MEMBER_BONUSRATE")
	private int memberBonusRate;

	@Column(name = "MEMBER_EMAIL", length = 500, unique = true)
	private String memberEmail;

	@Column(name = "MEMBER_PHONENUMBER", length = 500)
	private String memberPhoneNumber;

	@Column(name = "MEMBER_ENABLED")
	private boolean memberEnabled;
	
	@Column(name="MEMBER_GRADE")
	private int memberGrade;
	
	@Column(name="MEMBER_TOMO_USERNAME")
	private String memberTomoUsername;
	
	@Column(name="MEMBER_TOMO_EMAIL")
	private String memberTomoEmail;

	@Column(name = "MEMBER_ROLE", length = 500)
	private String memberRole;

	@OneToMany(
			fetch = FetchType.LAZY, 
			cascade = CascadeType.ALL,
			orphanRemoval = true,
			mappedBy = "buyerMemberId"
			)
	private List<Buyer> buyers;

}
