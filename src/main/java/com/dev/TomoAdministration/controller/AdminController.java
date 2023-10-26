package com.dev.TomoAdministration.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.MemberRepository;
import com.dev.TomoAdministration.service.EmailService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	EmailService emailService;
	
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
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(() -> {
        	String[] to = new String[1];
    		to[0] = memberRepository.findById(id).get().getMemberEmail();
        	
        	try {
        		emailService.sendEmail(to, "snstomo managercenterガイドメールです。", "あなたの登録審査が承認されました。これから正常にご利用いただけますので、ご使用上のご質問やご不明な点がございましたらadmin@qlix.co.jp メールでご連絡ください。");
        	}catch(MailSendException e) {
        	} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
    		
    		
        });
		
		return "redirect:/admin/memberRegistrationCheck";
	}
	
}
