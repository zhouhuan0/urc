package com.yks.urc.vo;

import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.List;

public class TaskParamVO {
	public String title;
	public Integer minutes;
	public Integer limit;
    public Integer pageSize;
    public List<String> platformCodes;

    public Boolean ifFinished;

	public Integer packageStatus = null;
	public Integer status = null;
    
    public List<Integer> moduleCodes;

	public Integer threadPoolSize = 1;

	public List<String> startEndTimes;

	public Date startTime;
	public Date endTime;

	public Integer month;
	
	public List<String> channelCodes;
	
	public Date modifyTime; 
	
	public Integer pushWmsLimit;
	
	public List<Integer> packageStatusList;
    public Date lastPoint;

    @Override
	public String toString() {
		return JSON.toJSONString(this);
	}


}
