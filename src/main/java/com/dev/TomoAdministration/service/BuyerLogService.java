package com.dev.TomoAdministration.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
	
	
	public List<BuyerLog> findByDate(
			String buyerLogUsername,
			String startDate, 
			String endDate)
			throws ParseException {

		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat bf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ("".equals(startDate) && "".equals(startDate)) {

			Date today = new Date();
			String day = bf.format(today);

			String start = day.substring(0, 10) + " 00:00:00";
			String end = day.substring(0, 10) + " 23:59:00";

			Date first = bf.parse(start);
			Date second = bf.parse(end);
			
			return buyerLogRepository.findAllByBuyerLogUsernameAndBuyerLogDateBetween(buyerLogUsername, first, second);

		} else if (!"".equals(startDate) && !"".equals(startDate) && startDate.equals(endDate)) {
			String start = startDate.substring(0, 10) + " 00:00:00";
			Date first = f.parse(start);
			Date second = f.parse(start);

			Calendar c = Calendar.getInstance();
			c.setTime(second);
			c.add(Calendar.DATE, 1);
			second = c.getTime();

			return buyerLogRepository.findAllByBuyerLogUsernameAndBuyerLogDateBetween(buyerLogUsername, first, second);

		} else if ("".equals(startDate) && !"".equals(endDate)) {

			Date second = f.parse(endDate);
			return buyerLogRepository.findAllByBuyerLogUsernameAndBuyerLogDateLessThan(buyerLogUsername, second);

		} else if (!"".equals(startDate) && "".equals(endDate)) {
			Date first = f.parse(startDate);
			return buyerLogRepository.findAllByBuyerLogUsernameAndBuyerLogDateGreaterThan(buyerLogUsername, first);
		} else {
			Date first = f.parse(startDate);
			Date second = f.parse(endDate);

			Calendar c = Calendar.getInstance();
			c.setTime(second);
			c.add(Calendar.DATE, 1);
			second = c.getTime();

			return buyerLogRepository.findAllByBuyerLogUsernameAndBuyerLogDateBetween(buyerLogUsername, first, second);
		}
	}
	
	
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
			buyer.setBuyerBonusRate(0l);
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













