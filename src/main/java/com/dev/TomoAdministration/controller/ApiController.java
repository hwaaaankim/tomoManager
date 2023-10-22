package com.dev.TomoAdministration.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dev.TomoAdministration.constant.Aes256Util;
import com.dev.TomoAdministration.dto.TokenInfo;
import com.dev.TomoAdministration.model.Buyer;
import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.BuyerRepository;
import com.dev.TomoAdministration.repository.MemberRepository;
import com.dev.TomoAdministration.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

	
	@Autowired
	MemberService memberService;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	BuyerRepository buyerRepository;
	
	@RequestMapping("/jwtTest")
	@ResponseBody
	public String jwtTest(Aes256Util util) throws JsonProcessingException, InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		TokenInfo info = new TokenInfo();
		
		ObjectMapper mapper = new ObjectMapper();
		
		String json = mapper.writeValueAsString(info);
		return util.AES_Encode(json);
	}
	
	@RequestMapping("/jwtDecodeTest")
	@ResponseBody
	public String jwtDecodeTest(Aes256Util util) throws JsonProcessingException, InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		TokenInfo info = new TokenInfo();
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(info);
		String enco = util.AES_Encode(json);
		String deco = util.AES_Decode(enco);
		
		
		return deco;
	}
	
	@RequestMapping(value = "/buyerRegistration")
	public String buyerRegistration(
			@RequestParam(name = "parent", required=false) String parent,
			@RequestParam(name = "grade", required=false) int grade,
			@RequestParam(name = "rate", required=false) int rate,
			@RequestParam(name = "username", required=false) String username,
			@RequestParam(name = "email", required=false) String email,
			Aes256Util util,
			Model model,
			Buyer buyer
			) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, JsonMappingException, JsonProcessingException {
		
		System.out.println(parent);
		System.out.println(rate);
		System.out.println(grade);
		System.out.println(username);
		System.out.println(email);
//		String json = util.AES_Decode(code);
//		ObjectMapper mapper = new ObjectMapper();
//		TokenInfo info = mapper.readValue(json, TokenInfo.class);
		buyer.setBuyerUsername(username);
		buyer.setBuyerEmail(email);
		buyer.setBuyerBonusRate(rate);
		buyer.setBuyerJoinDate(new Date());
		buyer.setBuyerMemberId(memberRepository.findByUsername(parent).get().getMemberId());
		buyerRepository.save(buyer);
		return buyer.toString();
	}
	
	@PostMapping("/memberJoin")
	public String memberJoin(
			Member member
			) {

		memberService.save(member);
		
		return "success";
	}
	
	@PostMapping("/adminJoin")
	public String adminJoin(
			Member member
			) {

		memberService.insertAdmin(member);
		
		return "success";
	}
	
}
