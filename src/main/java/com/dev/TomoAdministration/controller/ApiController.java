package com.dev.TomoAdministration.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

	
	@PostMapping(value = "/subMemberRegistration")
	public String subMemberRegistration(
			@RequestParam("code") String code,
			@RequestParam("username") String username,
			@RequestParam("email") String email
			) {
		
		System.out.println(code);
		System.out.println(username);
		System.out.println(email);
		
		return email;
	}
	
}
