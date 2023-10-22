package com.dev.TomoAdministration.dto;

import org.springframework.lang.Nullable;

import lombok.Data;

@Data
public class TokenInfo {

	// 인증타입 , Bearer
	private String parentUsername;
	private int bonusRate;
	@Nullable
	private int grade;
}
