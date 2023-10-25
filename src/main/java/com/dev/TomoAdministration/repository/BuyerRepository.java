package com.dev.TomoAdministration.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.TomoAdministration.model.Buyer;

@Repository
public interface BuyerRepository extends JpaRepository<Buyer, Long>{

	Optional<Buyer> findByBuyerUsername(String username);
}
