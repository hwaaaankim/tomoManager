package com.dev.TomoAdministration.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MemberService{

	@Bean(name = "saveBean")
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	
	@Autowired
	private MemberRepository memberRepository;

	public Member save(Member member) {
		String encodedPassword = passwordEncoder().encode(member.getPassword());
		member.setPassword(encodedPassword);
		member.setMemberEnabled(false);
		member.setMemberJoinDate(new Date());
		if(member.getMemberGrade() == 2) {
			member.setMemberRole("ROLE_MEMBER");
		}else if(member.getMemberGrade() == 3) {
			member.setMemberRole("ROLE_SUBMEMBER");
		}
		
		member.setMemberParentUsername(member.getMemberParentUsername());
		member.setMemberGrade(member.getMemberGrade());
		member.setMemberBonusRate(member.getMemberBonusRate());
		
		log.info("NEW MEMBER");

		return memberRepository.save(member);

	}
	
	
	
	public Member insertAdmin(Member member) {
		String encodedPassword = passwordEncoder().encode(member.getPassword());
		member.setPassword(encodedPassword);
		member.setMemberEnabled(true);
		member.setMemberJoinDate(new Date());
		member.setMemberRole("ROLE_ADMIN");
		member.setMemberParentUsername("SNSTOMO");
		member.setMemberBonusRate(100);
		member.setMemberGrade(1);
		
		log.info("NEW ADMIN");
		
		return memberRepository.save(member);

	}
	
	public void editProfile(Member member) {
		if(member.getPassword()!=null) {
			String encodedPassword = passwordEncoder().encode(member.getPassword());
			memberRepository.findById(member.getMemberId()).ifPresent(m->{
				m.setMemberAccountNumber(member.getMemberAccountNumber());
				m.setMemberBankName(member.getMemberBankName());
				m.setMemberBankPoint(member.getMemberBankPoint());
				m.setPassword(encodedPassword);
				m.setMemberTomoEmail(member.getMemberTomoEmail());
				m.setMemberTomoUsername(member.getMemberTomoUsername());
				m.setMemberName(member.getMemberName());
				m.setMemberPhoneNumber(member.getMemberPhoneNumber());
				m.setMemberEmail(member.getMemberEmail());
				memberRepository.save(m);
			});
		}else {
			memberRepository.findById(member.getMemberId()).ifPresent(m->{
				m.setMemberAccountNumber(member.getMemberAccountNumber());
				m.setMemberBankName(member.getMemberBankName());
				m.setMemberBankPoint(member.getMemberBankPoint());
				m.setMemberTomoEmail(member.getMemberTomoEmail());
				m.setMemberTomoUsername(member.getMemberTomoUsername());
				m.setMemberName(member.getMemberName());
				m.setMemberPhoneNumber(member.getMemberPhoneNumber());
				m.setMemberEmail(member.getMemberEmail());
				memberRepository.save(m);
			});	
		}
		
	}
}
