package com.dev.TomoAdministration.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.TomoAdministration.model.AccessLog;

@Repository
public interface AccessLogRepository extends JpaRepository<AccessLog, Long>{

}
