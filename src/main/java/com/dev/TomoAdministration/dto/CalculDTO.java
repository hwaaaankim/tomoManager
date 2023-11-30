package com.dev.TomoAdministration.dto;

import lombok.Data;

@Data
public class CalculDTO {

	private String startDate;
	private String endDate;
	private String paidDate;
	private Boolean sign;
	private Double price;
}
