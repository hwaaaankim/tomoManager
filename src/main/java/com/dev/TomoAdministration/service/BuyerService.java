package com.dev.TomoAdministration.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
			Long rate,
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
	
	public Page<Buyer> findByDate(
			Long memberId,
			Pageable pageable, 
			String startDate, 
			String endDate) throws ParseException {

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat bf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ("".equals(startDate) && "".equals(startDate)) {

			Date today = new Date();
			String day = bf.format(today);

			String start = day.substring(0, 10) + " 00:00:00";
			String end = day.substring(0, 10) + " 23:59:00";

			Date first = bf.parse(start);
			Date second = bf.parse(end);
			return buyerRepository.findAllByBuyerMemberIdAndBuyerJoinDateBetween(memberId, pageable, first, second);

		} else if (!"".equals(startDate) && !"".equals(startDate) && startDate.equals(endDate)) {
			String start = startDate.substring(0, 10) + " 00:00:00";
			Date first = f.parse(start);
			Date second = f.parse(start);

			Calendar c = Calendar.getInstance();
			c.setTime(second);
			c.add(Calendar.DATE, 1);
			second = c.getTime();

			return buyerRepository.findAllByBuyerMemberIdAndBuyerJoinDateBetween(memberId, pageable, first, second);

		} else if ("".equals(startDate) && !"".equals(endDate)) {

			Date second = f.parse(endDate);
			return buyerRepository.findAllByBuyerMemberIdAndBuyerJoinDateLessThan(memberId, pageable, second);

		} else if (!"".equals(startDate) && "".equals(endDate)) {
			Date first = f.parse(startDate);
			return buyerRepository.findAllByBuyerMemberIdAndBuyerJoinDateGreaterThan(memberId, pageable, first);
		} else {
			Date first = f.parse(startDate);
			Date second = f.parse(endDate);

			Calendar c = Calendar.getInstance();
			c.setTime(second);
			c.add(Calendar.DATE, 1);
			second = c.getTime();

			return buyerRepository.findAllByBuyerMemberIdAndBuyerJoinDateBetween(memberId, pageable, first, second);
		}
	}
	
	
}
