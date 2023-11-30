package com.dev.TomoAdministration.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dev.TomoAdministration.constant.Aes256Util;
import com.dev.TomoAdministration.dto.CalculDTO;
import com.dev.TomoAdministration.dto.CalculDateDTO;
import com.dev.TomoAdministration.dto.TokenInfo;
import com.dev.TomoAdministration.model.Buyer;
import com.dev.TomoAdministration.model.BuyerLog;
import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.BuyerLogRepository;
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
	BuyerLogRepository buyerLogRepository;
	
	@Autowired
	BuyerService buyerService;
	
	@GetMapping("/myInfo")
	public String myInfo(
			Model model,
			Principal principal
			) {
		
		Member member = memberRepository.findByUsername(principal.getName()).get();
		model.addAttribute("member", member);
		return "member/information/information";
	}
	
	@GetMapping("/clientManager")
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
	
	@GetMapping("/clientDetail/{id}")
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
	
	@GetMapping("/editInformation")
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
	
	@GetMapping("/calculation")
	public String calculation(
			Principal principal,
			Model model,
			@PageableDefault(size = 10) Pageable pageable
			) throws ParseException {
		// 접속자 검색 
		Member member = memberRepository.findByUsername(principal.getName()).get();
		List<CalculDateDTO> dateList = new ArrayList<CalculDateDTO>();
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat bf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String[] lastDay = {"31", "28", "31", "30", "31", "30", "31", "31", "30", "31", "30", "31"};
		String[] months = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
		Calendar memberRegistrationDate = Calendar.getInstance();
		memberRegistrationDate.setTime(member.getMemberJoinDate());
        String memberRegistrationResultDate = f.format(memberRegistrationDate.getTime());
        String memberRegistrationYear = memberRegistrationResultDate.substring(0, 4);
        String memberRegistrationMonth = memberRegistrationResultDate.substring(5, 7);
        String memberRegistrationDay = memberRegistrationResultDate.substring(8, 10);
        
        Calendar today = Calendar.getInstance();
        String todayResultDate = f.format(today.getTime());
        String todayYear = todayResultDate.substring(0, 4);
        String todayMonth = todayResultDate.substring(5, 7);
        String todayDay = todayResultDate.substring(8, 9);
        
        int betweenTwoDate = Integer.parseInt(todayMonth) 
        		- Integer.parseInt(memberRegistrationMonth) 
        		+ 1 
        		+ (Integer.parseInt(todayYear) - Integer.parseInt(memberRegistrationYear)) * 12;
       
    	int checkMonth = Integer.parseInt(memberRegistrationMonth); // 6
    	int checkYear = 0;
        for(int a = 0; a < betweenTwoDate; a++) {
        	CalculDateDTO dto = new CalculDateDTO();
        	String startDay = memberRegistrationDay;
        	
        	if(a > 0) {
        		startDay = "1";
        		checkMonth++;
        	}
        	if(checkMonth > 12) {
    			checkMonth = 1;
    		}
        	
    		dto.setStartDate(f.parse((Integer.parseInt(memberRegistrationYear) + checkYear)
    				+ "-" 
    				+ months[checkMonth-1] 
    				+ "-" 
    				+ startDay));
    		dto.setEndDate(f.parse((Integer.parseInt(memberRegistrationYear) + (checkYear))
    				+ "-" 
    				+ months[checkMonth-1]
    				+ "-" 
    				+ lastDay[Integer.parseInt(months[checkMonth-1])-1]));
    		if(checkMonth%12 == 0) {
				checkYear++;
        	}
        	dateList.add(dto);
        }
        
		// 접속자의 모든 판매자 검색
		List<Buyer> buyers = buyerRepository.findAllByBuyerMemberIdOrderByBuyerJoinDateDesc(member.getMemberId());
		List<CalculDTO> cDTOs = new ArrayList<CalculDTO>();
		for(CalculDateDTO date : dateList) {
			CalculDTO cDto = new CalculDTO();
			cDto.setStartDate(f.format(date.getStartDate()));
			cDto.setEndDate(f.format(date.getEndDate()));
			Double periodPrice = 0d;
			for(Buyer buyer : buyers) {
				Double price = 0d;
				for(BuyerLog log : buyerLogRepository.findAllByBuyerLogUsernameAndBuyerLogDateBetween(buyer.getBuyerUsername(), date.getStartDate(), date.getEndDate())) {
					String money = "";
					if(log.getBuyerLogPrice().substring(1).replaceAll(",", "").indexOf(".") > 0) {
						money = log.getBuyerLogPrice().substring(1).replaceAll(",", "").substring(0, log.getBuyerLogPrice().substring(1).replaceAll(",", "").indexOf("."));
					}else {
						money = log.getBuyerLogPrice().substring(1).replaceAll(",", "");
					}
					price += (Integer.parseInt(money)*buyer.getBuyerBonusRate())/100;
				}
				if(buyer.getBuyerUsername().equals("takumi57594")) {
					System.out.println(buyer.getBuyerUsername() + "의 정산 금액은 : " + price);
				}
				periodPrice+=price;
			}
			cDto.setSign(false);
			cDto.setPrice(periodPrice);
			cDto.setPaidDate("-");
			cDTOs.add(cDto);
		}
		for(CalculDTO d : cDTOs) {
			System.out.println(d.getStartDate());
			System.out.println(d.getEndDate());
			System.out.println(d.getPrice());
		}
		
		
		PageRequest pageRequest = PageRequest.of(0, 100);
		int start = (int) pageRequest.getOffset();
		int end = Math.min((start + pageRequest.getPageSize()), cDTOs.size());
		Page<CalculDTO> logs = new PageImpl<>(cDTOs.subList(start, end), pageRequest, cDTOs.size());
		int startPage = Math.max(1, logs.getPageable().getPageNumber() - 4);
		int endPage = Math.min(logs.getTotalPages(), logs.getPageable().getPageNumber() + 4);
		model.addAttribute("buyers", logs);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		return "member/calculation/calculation";
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
