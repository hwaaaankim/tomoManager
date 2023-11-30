package com.dev.TomoAdministration.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.TomoAdministration.model.Buyer;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long>{

	Optional<Buyer> findByBuyerUsername(String username);
	
	List<Buyer> findAllByBuyerMemberIdOrderByBuyerJoinDateDesc(Long memberId);

	Page<Buyer> findAllByBuyerMemberIdOrderByBuyerJoinDateDesc(Long memberId, Pageable pageable);
	
	Page<Buyer> findAllByBuyerUsernameAndBuyerMemberIdOrderByBuyerJoinDateDesc(Long memberId, Pageable pageable, String name);
	
	Page<Buyer> findAllByBuyerEmailAndBuyerMemberIdOrderByBuyerJoinDateDesc(Long memberId, Pageable pageable, String phone);
	
	Page<Buyer> findAllByBuyerMemberIdAndBuyerJoinDateLessThan(Long memberId, Pageable pageable,Date date);
	
	Page<Buyer> findAllByBuyerMemberIdAndBuyerJoinDateGreaterThan(Long memberId, Pageable pageable,Date date);
	
	Page<Buyer> findAllByBuyerMemberIdAndBuyerJoinDateBetween(Long memberId, Pageable pageable, Date startDate, Date endDate);
}
