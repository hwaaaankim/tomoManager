package com.dev.TomoAdministration.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.TomoAdministration.model.Buyer;
import com.dev.TomoAdministration.repository.BuyerRepository;

@Service
public class BuyerService {

	@Autowired
	BuyerRepository buyerRepository;
	
	public void buyerRegistration(
			Long parent,
			String username,
			String email,
			int rate,
			Boolean sign
			) {
		Buyer buyer = new Buyer();
		buyer.setBuyerSign(sign);
		buyer.setBuyerUsername(username);
		buyer.setBuyerEmail(email);
		buyer.setBuyerBonusRate(rate);
		buyer.setBuyerJoinDate(new Date());
		buyer.setBuyerMemberId(parent);
		buyerRepository.save(buyer);
	}
	
	
}
