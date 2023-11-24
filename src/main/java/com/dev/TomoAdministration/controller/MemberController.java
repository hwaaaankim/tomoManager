package com.dev.TomoAdministration.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.ParseException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dev.TomoAdministration.constant.Aes256Util;
import com.dev.TomoAdministration.dto.TokenInfo;
import com.dev.TomoAdministration.model.Buyer;
import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.BuyerRepository;
import com.dev.TomoAdministration.repository.MemberRepository;
import com.dev.TomoAdministration.service.BuyerService;
import com.dev.TomoAdministration.service.EmailService;
import com.dev.TomoAdministration.service.MemberService;
import com.dev.TomoAdministration.service.SMSService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/member")
public class MemberController {
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	SMSService smsService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	BuyerRepository buyerRepository;
	
	@Autowired
	BuyerService buyerService;
	
	@RequestMapping("/myInfo")
	public String myInfo(
			Model model,
			Principal principal
			) {
		
		Member member = memberRepository.findByUsername(principal.getName()).get();
		model.addAttribute("member", member);
		return "member/information/information";
	}
	
	@RequestMapping("/clientManager")
	public String clientManager(
			Principal principal,
			Model model,
			@PageableDefault(size = 10) Pageable pageable,
			@RequestParam(required = false) String searchType,
			@RequestParam(required = false) String searchWord,
			@RequestParam(required = false) String startDate, 
			@RequestParam(required = false) String endDate
			) throws ParseException {
		Member member = memberRepository.findByUsername(principal.getName()).get();
		Page<Buyer> buyers = null;
		if(searchType==null || "none".equals(searchType)) {
			buyers = buyerRepository.findAllByBuyerMemberIdOrderByBuyerJoinDateDesc(member.getMemberId(), pageable);
		}else if("username".equals(searchType)) {
			if("".equals(searchWord)) {
				buyers = buyerRepository.findAllByBuyerMemberIdOrderByBuyerJoinDateDesc(member.getMemberId(), pageable);
			}else {
				buyers = buyerRepository.findAllByBuyerUsernameAndBuyerMemberIdOrderByBuyerJoinDateDesc(member.getMemberId(), pageable, searchWord);
			}
		}else if("email".equals(searchType)) {
			if("".equals(searchWord)) {
				buyers = buyerRepository.findAllByBuyerMemberIdOrderByBuyerJoinDateDesc(member.getMemberId(), pageable);
			}else {
				buyers = buyerRepository.findAllByBuyerEmailAndBuyerMemberIdOrderByBuyerJoinDateDesc(member.getMemberId(), pageable, searchWord);
			}
		}else if("period".equals(searchType)) {
			buyers = buyerService.findByDate(member.getMemberId(), pageable, startDate, endDate);
		}else {
			buyers = buyerRepository.findAllByBuyerMemberIdOrderByBuyerJoinDateDesc(member.getMemberId(), pageable);
		}
		
		int startPage = Math.max(1, buyers.getPageable().getPageNumber() - 4);
		int endPage = Math.min(buyers.getTotalPages(), buyers.getPageable().getPageNumber() + 4);
		model.addAttribute("buyers", buyers);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("searchType", searchType);
		return "member/buyer/clientManager";
	}
	
	@RequestMapping("/clientDetail/{id}")
	public String clientDetail(
			@PathVariable Long id,
			Model model
			) {
		
		model.addAttribute("buyer", buyerRepository.findById(id).get());
		return "member/buyer/clientDetail";
	}
	
	@PostMapping("/editProcess")
	public String editProcess(
			Member member
			) {
		memberService.editProfile(member);
		return "redirect:/member/myInfo";
	}
	
	@RequestMapping("/editInformation")
	public String editInformation(
			Model model,
			Principal principal
			) {
		
		Member member = memberRepository.findByUsername(principal.getName()).get();
		model.addAttribute("member", member);
		return "member/information/editInformation";
	}
	
	@RequestMapping("/subMemberRegistrationCheck")
	public String subMemberRegistrationCheck(
			Model model,
			@PageableDefault(size = 10) Pageable pageable,
			Principal principal
			) {
		
		Page<Member> members = memberRepository.findAllByMemberEnabledAndMemberParentUsername(pageable, false, principal.getName());
		
		int startPage = Math.max(1, members.getPageable().getPageNumber() - 4);
		int endPage = Math.min(members.getTotalPages(), members.getPageable().getPageNumber() + 4);
		
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("members", members);
		
		return "member/dealer/memberRegistrationCheck";
	}
	
	@RequestMapping("/changeSubMemberStatus/{id}")
	public String changeMemberStatus(
			@PathVariable Long id
			) {
		memberRepository.findById(id).ifPresent(c->{
			c.setMemberEnabled(true);
			memberRepository.save(c);
		});
		return "redirect:/member/subMemberRegistrationCheck";
	}
	
	@RequestMapping("/makeLink")
	public String makeLink(
			Principal principal,
			Model model
			) {
		model.addAttribute("max", memberRepository.findByUsername(principal.getName()).get().getMemberBonusRate());
		model.addAttribute("parent", memberRepository.findByUsername(principal.getName()).get().getMemberRole());
		return "member/information/makeLink";
	}
	
	
	
	@PostMapping("/createLink")
	public String createLink(
			TokenInfo info,
			Principal principal,
			Model model,
			String code,
			Aes256Util util
			) throws JsonProcessingException, InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		if(info.getParentUsername().equals("ROLE_ADMIN")) {
			info.setGrade(2);
		}else {
			info.setGrade(3);
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(info);
		String sign = util.AES_Encode(json);
		if(code.equals("buyer")) {
			String link = "snstomo.co.jp/?parent=" + principal.getName()+"&grade="+info.getGrade()+"&rate="+info.getBonusRate();
			model.addAttribute("buyerLink", link);
		}else {
			String link = "snstomomanager.com/signup?parent=" + info.getParentUsername() + "&grade=" + info.getGrade() + "&rate="+info.getBonusRate();
			model.addAttribute("dealerLink", link);
		}
		model.addAttribute("max", memberRepository.findByUsername(principal.getName()).get().getMemberBonusRate());
		model.addAttribute("parent", principal.getName());
		return "member/information/makeLink";
	}
	
	
}
