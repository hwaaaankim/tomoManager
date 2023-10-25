package com.dev.TomoAdministration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.MemberRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	MemberRepository memberRepository;
	
	@RequestMapping("/memberRegistrationCheck")
	public String memberRegistrationCheck(
			Model model,
			@PageableDefault(size = 10) Pageable pageable
			) {
		
		Page<Member> members = memberRepository.findAllByMemberEnabled(pageable, false);
		
		int startPage = Math.max(1, members.getPageable().getPageNumber() - 4);
		int endPage = Math.min(members.getTotalPages(), members.getPageable().getPageNumber() + 4);
		
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("members", members);
		
		return "administration/member/memberRegistrationCheck";
	}
	
	@RequestMapping("/changeMemberStatus/{id}")
	public String changeMemberStatus(
			@PathVariable Long id
			) {
		memberRepository.findById(id).ifPresent(c->{
			c.setMemberEnabled(true);
			memberRepository.save(c);
		});
		return "redirect:/admin/memberRegistrationCheck";
	}
	
}
