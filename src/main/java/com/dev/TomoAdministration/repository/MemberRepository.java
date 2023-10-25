package com.dev.TomoAdministration.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.TomoAdministration.model.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>{

	Optional<Member> findByUsername(String username);
	
	Page<Member> findAllByMemberEnabled(Pageable pageable, Boolean enabled);
	Page<Member> findAllByMemberEnabledAndMemberParentUsername(Pageable pageable, Boolean enabled, String parent);
}
