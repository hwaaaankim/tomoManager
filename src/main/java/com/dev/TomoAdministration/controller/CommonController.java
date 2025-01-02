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

import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dev.TomoAdministration.constant.Aes256Util;
import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.MemberRepository;
import com.dev.TomoAdministration.service.EmailService;
import com.dev.TomoAdministration.service.MemberService;
import com.dev.TomoAdministration.service.SMSService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Controller
public class CommonController {

	@Autowired
	MemberService memberService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	SMSService smsService;
	
	@Autowired
	MemberRepository memberRepository;
	
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
	
	@RequestMapping("/signup")
	public String signup(
			@RequestParam(required = false, defaultValue = "NONE") String parent,
			@RequestParam(required = false, defaultValue = "2") Integer grade,
			@RequestParam(required = false, defaultValue = "30") Integer rate,
			
			Aes256Util util,
			Model model
			) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, JsonMappingException, JsonProcessingException, EncoderException {
		
		if(parent.equals("NONE")) {
			model.addAttribute("memberGrade", 2);
			model.addAttribute("memberBonusRate", 30);
			model.addAttribute("memberParentUsername", "snstomo");
		}else {
			model.addAttribute("memberParentUsername", parent);
			model.addAttribute("memberGrade", grade);
			model.addAttribute("memberBonusRate", rate);
		}
		
		return "all/signup";
	}
	@PostMapping("/signupProcess")
	public String signupProcess(Member member) throws EncoderException {
		List<Member> adminUsers = memberRepository.findAllByMemberRole("ROLE_ADMIN");
		
		if(member.getMemberTomoEmail().equals("")) {
			member.setMemberTomoEmail("NONE");
		}
		if(member.getMemberTomoUsername().equals("")) {
			member.setMemberTomoUsername("NONE");
		}	
		if(member.getMemberBankName().equals("")) {
			member.setMemberBankName("NONE");
		}
		if(member.getMemberBankPoint().equals("")) {
			member.setMemberBankPoint("NONE");
		}
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(() -> {
        	String[] to = new String[1];
    		to[0] = member.getMemberEmail();
        	
        	try {
        		emailService.sendEmail(to, "snstomo managerに参加していただきありがとうございます。", "使用方法に関するご質問やご質問がある場合は、admin@qlix.co.jpメールでご連絡ください。");
        	}catch(MailSendException e) {
        	} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
    		
    		
        });
		
		
//		for(Member m : adminUsers) {
//			smsService.sendMessage(m.getMemberPhoneNumber(), "판매자 회원가입이 발생하였습니다.");
//		}
		memberService.save(member);
		return "all/signin";
	}
}
