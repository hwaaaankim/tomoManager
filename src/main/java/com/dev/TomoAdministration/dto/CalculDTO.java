package com.dev.TomoAdministration.dto;

import lombok.Data;

@Data
public class CalculDTO {

	private String username;
	private String balance;
	private String amount;
	private String method;
	private String status;
	private String memo;
	private String startDate;
	private String endDate;

	
	private String paidDate;
	private Boolean sign;
	private Double price;
}
