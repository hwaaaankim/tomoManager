package com.dev.TomoAdministration.controller;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.EncoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dev.TomoAdministration.constant.Aes256Util;
import com.dev.TomoAdministration.dto.TokenInfo;
import com.dev.TomoAdministration.model.AccessLog;
import com.dev.TomoAdministration.model.Buyer;
import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.BuyerRepository;
import com.dev.TomoAdministration.repository.MemberRepository;
import com.dev.TomoAdministration.service.AccessLogService;
import com.dev.TomoAdministration.service.BuyerLogService;
import com.dev.TomoAdministration.service.BuyerService;
import com.dev.TomoAdministration.service.EmailService;
import com.dev.TomoAdministration.service.MemberService;
import com.dev.TomoAdministration.service.SMSService;
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
	
	@Autowired
	BuyerService buyerService;
	
	@Autowired
	BuyerLogService buyerLogService;
	
	@Autowired
	SMSService smsService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	AccessLogService accessLogService;
	
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
	
	@PostMapping(value="/paymentLogging")
	public String paymentLogging(
			@RequestParam(name = "username", required = false) String username,
			@RequestParam(name = "price", required = false) String price,
			@RequestParam(name = "type", required = false) String type,
			@RequestParam(name="peopleName", required = false) String peopleName
			) throws EncoderException {
		
		List<Member> adminUsers = memberRepository.findAllByMemberRole("ROLE_ADMIN");
		for(Member member : adminUsers) {
			smsService.sendMessage(member.getMemberPhoneNumber(), 
					peopleName+"님(TOMO ID : " + username + ")님이 " + type + "을 통해 " + price + "엔 충전을"
							+ " 신청하였습니다." );
		}
		return "success";
	}
	
	@PostMapping(value="/accessLogging")
	public String accessLogging(
			@RequestParam(name = "device", required = false) String device,
			@RequestParam(name = "exAddress", required = false) String exAddress,
			@RequestParam(name = "ipAddress", required = false) String ipAddress,
			@RequestParam(name = "language", required = false) String language
			) throws EncoderException {
		AccessLog log = new AccessLog();
		
		log.setAccessLogDevice(device);
		log.setAccessLogIp(ipAddress);
		log.setAccessLogLang(language);
		log.setAccessLogEx(exAddress);
		accessLogService.insertLog(log);		
		return "success";
	}
	
	@PostMapping(value="/buyerLogging")
	public String buyerLogging(
			@RequestParam(name = "link", required = false) String link,
			@RequestParam(name = "price", required = false) String price,
			@RequestParam(name = "username", required = false) String username
			) throws EncoderException {
		List<Member> adminUsers = memberRepository.findAllByMemberRole("ROLE_ADMIN");
//		for(Member member : adminUsers) {
//			smsService.sendMessage(member.getMemberPhoneNumber(), "최종 구매자의 구매 활동이 발생하였습니다.");
//		}
		buyerLogService.buyerLogging(link, username, price);
		return "success";
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
			) throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, JsonMappingException, JsonProcessingException, EncoderException {
		Long finalRate = (long)rate;
		Boolean sign = true;
		if(parent.equals("snstomo")) {
			sign = false;
			buyerService.buyerRegistration(
					memberRepository.findByUsername("admin").get().getMemberId(), 
					username, 
					email, 
					finalRate, 
					sign);
		}else {
			buyerService.buyerRegistration(
					memberRepository.findByUsername(parent).get().getMemberId(), 
					username, 
					email, 
					finalRate, 
					sign);
		}
		
//		List<Member> adminUsers = memberRepository.findAllByMemberRole("ROLE_ADMIN");
//		for(Member member : adminUsers) {
//			smsService.sendMessage(member.getMemberPhoneNumber(), "최종 구매자 회원가입이 발생하였습니다.");
//		}
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.submit(() -> {
        	String[] to = new String[1];
    		to[0] = email;
        	
        	try {
        		emailService.sendEmail(to, "snstomoに会員登録していただきありがとうございます。 ", username + "様\r\n" + 
        				"\r\n" + 
        				"この度はSNS TOMOにご登録いただきまして誠にありがとうございます。\r\n" + 
        				"国内最高品質のSNS拡散サービス「SNS TOMO」でお客様のSNSアカウントを拡散してみてください。\r\n" + 
        				"\r\n" + 
        				"新規登録をしていただいたお客様を対象に20%のボーナスポイント支給キャンペーンを実施しております。\r\n" + 
        				"今だけの特別なキャンペーンとなっております。\r\n" + 
        				"是非この機会にポイントチャージをしてSNS TOMOをご利用くださいませ。\r\n" + 
        				"\r\n" + 
        				"=========================\r\n" + 
        				"ボーナスポイントキャンペーンを確認しましたか？\r\n" + 
        				"=========================\r\n" + 
        				"\r\n" + 
        				"期間限定キャンペーンを行っております。\r\n" + 
        				"\r\n" + 
        				"20％ボーナスポイント贈呈\r\n" + 
        				"期限は2023/10/31まで\r\n" + 
        				"\r\n" + 
        				"【決済可能手段】\r\n" + 
        				"クレジットカード、口座振込、PayPay、Paypal\r\n" + 
        				"\r\n" + 
        				"▼ ポイントチャージはこちら\r\n" + 
        				"https://snstomo.co.jp/addfunds\r\n" + 
        				"\r\n" + 
        				"=========================\r\n" + 
        				"SNS TOMOで拡散できるSNS\r\n" + 
        				"=========================\r\n" + 
        				"\r\n" + 
        				"・Instagram\r\n" + 
        				"・Twitter\r\n" + 
        				"・TikTok\r\n" + 
        				"・YouTube\r\n" + 
        				"・Facebook\r\n" + 
        				"・Thread\r\n" + 
        				"  & etc\r\n" + 
        				"\r\n" + 
        				"=========================\r\n" + 
        				"ログイン・お問い合わせ\r\n" + 
        				"=========================\r\n" + 
        				"\r\n" + 
        				"▼ ログイン\r\n" + 
        				"https://snstomo.co.jp/#signin\r\n" + 
        				"▼ お問い合わせ\r\n" + 
        				"Email : admin@qlix.co.jp\r\n" + 
        				"\r\n" + 
        				"\r\n" + 
        				"\r\n" + 
        				"※ パスワードを忘れてしまった場合はこちら。\r\n" + 
        				"https://snstomo.co.jp/resetpassword\r\n" + 
        				"\r\n" + 
        				"━━━━━━━━━━━━━━━━━━━━\r\n" + 
        				"運営会社 株式会社Qlix\r\n" + 
        				"━━━━━━━━━━━━━━━━━━━━");
        	}catch(MailSendException e) {
        	} catch (InterruptedException e) {
        		// TODO Auto-generated catch block
        		e.printStackTrace();
        	}
    		
    		
        });
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
