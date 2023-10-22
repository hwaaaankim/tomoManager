package com.dev.TomoAdministration.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dev.TomoAdministration.constant.Aes256Util;
import com.dev.TomoAdministration.dto.TokenInfo;
import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.service.EmailService;
import com.dev.TomoAdministration.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class CommonController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	EmailService emailService;
	
	@RequestMapping("/emailTest")
	@ResponseBody
	public String emailTest() {
		List<String> list = new ArrayList<String>();
		list.add("admin@atrt.co.kr");
		list.add("contact@atrt.co.kr");
		list.add("teriwoo48@gmail.co.kr");
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(() -> {
        	String[] to = new String[list.size()];
        	int i=0;
        	for(String a : list) {
        		to[i] = a;
        		i++;
        	}
        	
        	try {
        		emailService.sendEmail(to, "고객 문의 발생 - 웹서버 발송", "test 입니다.");
        	}catch(MailSendException e) {
        	} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
    		
    		
        });

        executorService.shutdown();
		return "success";
	}
	
	@RequestMapping({"/index", "/"})
	public String index() {
		
		return "all/index";
	}
	
	@RequestMapping("/signin")
	public String signin() {
		
		return "all/signin";
	}
	
	@RequestMapping("/signup/{code}")
	public String signup(
			@PathVariable(required = false) String code,
			Aes256Util util,
			Model model
			) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, JsonMappingException, JsonProcessingException {
		String json = util.AES_Decode(code);
		ObjectMapper mapper = new ObjectMapper();
		TokenInfo info = mapper.readValue(json, TokenInfo.class);
		model.addAttribute("memberParentUsername", info.getParentUsername());
		model.addAttribute("memberGrade", info.getGrade());
		model.addAttribute("memberBonusRate", info.getBonusRate());
		return "all/signup";
	}
	
	@RequestMapping("/signup")
	public String signup(
			Model model
			){
		model.addAttribute("memberGrade", 2);
		model.addAttribute("memberBonusRate", 30);
		model.addAttribute("memberParentUsername", "snstomo");
		return "all/signup";
	}
	
	@PostMapping("/signupProcess")
	public String signupProcess(Member member) {
		memberService.save(member);
		return "all/signin";
	}
}
