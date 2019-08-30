package com.yks.urc.userValidate.bp.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yks.urc.cache.bp.api.ICacheBp;
import com.yks.urc.entity.UserTicketDO;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.UserTicketMapper;
import com.yks.urc.userValidate.bp.api.ITicketUpdateBp;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TicketUpdateBpImpl implements ITicketUpdateBp {
    private static Logger logger = LoggerFactory.getLogger(TicketUpdateBpImpl.class);

    Map<String, UserTicketDO> mapUser = new ConcurrentHashMap<>();

    @Autowired
    ICacheBp cacheBp;

    @Override
    public void refreshExpiredTime(String userName,String deviceType, String ticket) {
        if (StringUtility.isNullOrEmpty(userName)) return;

        try {
            cacheBp.refreshUserExpiredTime(userName,deviceType);
        }
        catch(Exception ex){
            logger.error(String.format("refresh redis ERROR:%s %s", userName, ticket), ex);
        }

        String key = StringUtility.md5_NoException(String.format("%s_%s", userName, StringUtility.addEmptyString(deviceType)));
        // 如果map中有，则表示还没入库，不需要处理
        if (mapUser.containsKey(key)) return;
        UserTicketDO ut = new UserTicketDO();
        ut.setUserName(userName);
        ut.setDeviceType(deviceType);
        ut.setTicket(ticket);
        mapUser.put(key, ut);
    }

    @Autowired
    private UserTicketMapper userTicketMapper;

    Long EXPIRE_TIME = 2 * 1 * 60 * 60 * 1000L;

    @Override
    public void dump2Db() {
        try {
            // 将map copy一份，再删除旧map元素，再写库
            if (mapUser.size() == 0) return;

            Map<String, UserTicketDO> mapCp = StringUtility.json2ObjNew(StringUtility.obj2Json(mapUser),
                    new TypeReference<Map<String, UserTicketDO>>() {
                    });
            if (mapCp == null || mapCp.size() == 0) return;
            for (Map.Entry<String, UserTicketDO> en : mapCp.entrySet()) {
                try {
                    UserTicketDO ut=en.getValue();
                    if (ut == null || StringUtils.isBlank(ut.getUserName())) {
                        continue;
                    }
                    userTicketMapper.updateExpiredTime(ut.getUserName(), ut.getDeviceType(), new Date(new Date().getTime() + EXPIRE_TIME));
                    mapUser.remove(en.getKey());
                } catch (Exception ex) {
                    logger.error(String.format("%s %s", en.getKey(), en.getValue()), ex);
                }
            }
        } catch (Exception ex) {
            logger.error(StringUtility.toJSONString_NoException(mapUser), ex);
        }
    }
}
