package com.yks.urc.log;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.RollingFileAppender;
 

public class Log4jRollingFileAppender extends RollingFileAppender {
 
	public void setFile(String file) {
	    String val = file.trim();
	    fileName = val.substring(0, val.lastIndexOf("/"))+"/"+new SimpleDateFormat("yyyyMMdd").format(new Date())+val.substring(val.lastIndexOf("/"), val.lastIndexOf("."))+"_"+new SimpleDateFormat("yyyyMMdd_HH").format(new Date())+val.substring(val.lastIndexOf("."), val.length());
	}
	
}
