/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author lwx
 * @create 2018/11/26
 * @since 1.0.0
 */
package com.yks.urc.user.bp.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.UrcLog;
import com.yks.urc.mapper.UrcLogMapper;
import com.yks.urc.user.bp.api.IUrcLogBp;
import com.yks.urc.vo.UserVO;

@Component
public class UrcLogBpImpl implements IUrcLogBp {

    private static Logger logger = LoggerFactory.getLogger(UrcLogBpImpl.class);

    ExecutorService service = Executors.newFixedThreadPool(3);

    @Autowired
    private UrcLogMapper urcLogMapper;
    @Autowired
    private ICacheBp cacheBp;

	@Override
	public void insertUrcLog(UrcLog urcLog) {
		try {
			UserVO getU =cacheBp.getUser(urcLog.getUserName());
			urcLog.setComputerIp(null != getU?getU.ip:"");
			urcLog.setOperateTime(new Date());
			urcLogMapper.insert(urcLog);
			/*service.submit(new Runnable() {
	            @Override
	            public void run() {
	                try {
	                	urcLogMapper.insert(urcLog);
	                } catch (Exception e) {
	                    logger.error("UrcLogBpImpl insertUrcLog error! urcLog:{},e:{}",urcLog.toString(), e);
	                }
	            }
	        });*/
		} catch (Exception e) {
			logger.error("insertUrcLog error! urclog:{},e:{}",urcLog.toString(),e);
		}
		
		
	}


	@Override
	public List<UrcLog> selectUrcLogByConditions(Map<String, Object> conditionsMap) {
		return urcLogMapper.selectUrcLogByConditions(conditionsMap);
	}
}
