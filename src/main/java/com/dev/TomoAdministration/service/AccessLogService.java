package com.dev.TomoAdministration.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dev.TomoAdministration.model.AccessLog;
import com.dev.TomoAdministration.repository.AccessLogRepository;

@Service
public class AccessLogService {

	@Autowired
	AccessLogRepository accessLogRepository;
	
	public void insertLog(AccessLog accessLog) {
		
		Date today = new Date();
		accessLog.setAccessLogDate(today);
		accessLogRepository.save(accessLog);
		
	}
}
