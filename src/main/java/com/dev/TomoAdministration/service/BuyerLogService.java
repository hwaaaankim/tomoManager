package com.dev.TomoAdministration.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.TomoAdministration.model.Buyer;
import com.dev.TomoAdministration.model.BuyerLog;
import com.dev.TomoAdministration.repository.BuyerLogRepository;
import com.dev.TomoAdministration.repository.BuyerRepository;

@Service
public class BuyerLogService {

	@Autowired
	BuyerLogRepository buyerLogRepository;
	
	@Autowired
	BuyerRepository buyerRepository;
	
	public void buyerLogging(
			String link,
			String username,
			String price
			
			) {
		
		if(buyerRepository.findByBuyerUsername(username).isPresent()) {
			BuyerLog log = new BuyerLog();
			log.setBuyerLogDate(new Date());
			log.setBuyerLogBuyerId(buyerRepository.findByBuyerUsername(username).get().getBuyerId());
			log.setBuyerLogLink(link);
			log.setBuyerLogPrice(price);
			log.setBuyerLogUsername(username);
			
			buyerLogRepository.save(log);
			
		}else {
			Buyer buyer = new Buyer();
			buyer.setBuyerBonusRate(0);
			buyer.setBuyerEmail("NONE");
			buyer.setBuyerJoinDate(new Date());
			buyer.setBuyerMemberId(1L);
			buyer.setBuyerUsername(username);
			buyer.setBuyerSign(false);
			
			BuyerLog log = new BuyerLog();
			log.setBuyerLogDate(new Date());
			log.setBuyerLogBuyerId(buyerRepository.save(buyer).getBuyerId());
			
			log.setBuyerLogLink(link);
			log.setBuyerLogPrice(price);
			log.setBuyerLogUsername(username);
			
			buyerLogRepository.save(log);
		}
		
	}
}













