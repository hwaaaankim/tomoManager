package com.dev.TomoAdministration.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.TomoAdministration.dto.CalculDTO;
import com.dev.TomoAdministration.dto.CalculDateDTO;
import com.dev.TomoAdministration.model.Buyer;
import com.dev.TomoAdministration.model.BuyerLog;
import com.dev.TomoAdministration.model.Member;
import com.dev.TomoAdministration.repository.BuyerLogRepository;
import com.dev.TomoAdministration.repository.BuyerRepository;

@Service
public class CalculService {

	@Autowired
	BuyerLogRepository buyerLogRepository;
	
	@Autowired
	BuyerRepository buyerRepository;
	
	public List<CalculDTO> calculation(
			Member member
			) throws NumberFormatException, ParseException{
		
		List<CalculDateDTO> dateList = new ArrayList<CalculDateDTO>();
		
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		
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
					price += (Integer.parseInt(money)*(member.getMemberBonusRate() - buyer.getBuyerBonusRate()))/100;
				}
				periodPrice+=price;
			}
			cDto.setSign(false);
			cDto.setPrice(periodPrice);
			cDto.setPaidDate("-");
			cDTOs.add(cDto);
		}
		
		return cDTOs;
		
	}
	
	
}
