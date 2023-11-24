package com.dev.TomoAdministration.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="tb_access_log")
public class AccessLog {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="ACCESS_LOG_ID")
	private Long accessLogId;
	
	@Column(name="ACCESS_LOG_DEVICE")
	private String accessLogDevice;
	
	@Column(name="ACCESS_LOG_IP")
	private String accessLogIp;
	
	@Column(name="ACCESS_LOG_EX")
	private String accessLogEx;
	
	@Column(name="ACCESS_LOG_LANG")
	private String accessLogLang;
	
	@Column(name="ACCESS_LOG_DATE")
	private Date accessLogDate;
	

}
