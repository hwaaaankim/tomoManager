package com.dev.TomoAdministration.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.TomoAdministration.model.BuyerLog;

@Repository
public interface BuyerLogRepository extends JpaRepository<BuyerLog, Long>{
	
	
	List<BuyerLog> findAllByBuyerLogUsernameAndBuyerLogDateLessThan(String buyerLogUsername, Date date);
	
	List<BuyerLog> findAllByBuyerLogUsernameAndBuyerLogDateGreaterThan(String buyerLogUsername, Date date);
	
	List<BuyerLog> findAllByBuyerLogUsernameAndBuyerLogDateBetween(String buyerLogUsername, Date startDate, Date endDate);

}
