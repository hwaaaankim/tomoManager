package com.dev.TomoAdministration.dto;

import java.time.LocalDateTime;

public class MailSendResult {
	private final String username;
	private final String email;
	private final boolean success;
	private final String error; // 성공이면 빈 문자열
	private final LocalDateTime sentAt; // 시각

	public MailSendResult(String username, String email, boolean success, String error, LocalDateTime sentAt) {
		this.username = username;
		this.email = email;
		this.success = success;
		this.error = error == null ? "" : error;
		this.sentAt = sentAt;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getError() {
		return error;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}
}