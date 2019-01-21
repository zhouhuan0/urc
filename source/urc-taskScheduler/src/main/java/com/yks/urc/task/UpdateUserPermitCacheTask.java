package com.yks.urc.task;

import com.yks.urc.cache.bp.api.IUpdateAffectedUserPermitCache;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserAffectedMapper;
import com.yks.urc.permitStat.bp.api.IPermitStatBp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 版权：Copyright by www.youkeshu.com
 * 描述：代码注释以及格式化示例
 * 创建人：@author Songguanye
 * 创建时间：2019/1/19 12:19
 * 修改理由：
 * 修改内容：
 */
@Component
public class UpdateUserPermitCacheTask {
    private static Logger logger = LoggerFactory.getLogger(UpdateUserPermitCacheTask.class);
    private static final String SUPER_ADMINISTRATOR = "superAdministrator";
    @Autowired
    private IUserAffectedMapper userAffectedMapper;

    @Autowired
    private IPermitStatBp permitStatBp;
    @Autowired
    private IUpdateAffectedUserPermitCache updateAffectedUserPermitCache;
    @Autowired
    private IRoleMapper roleMapper;

    /**
     * 定时刷新UserPermitCache
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void updateUserPermitCache(){
        List<String> userNames = userAffectedMapper.selectAffectedUserList();
        try {
            if (!CollectionUtils.isEmpty(userNames)) {
                //去重
                List<String> userNameList = userNames.stream().distinct().collect(Collectors.toList());
                logger.info("Start updateUserPermitCache");
                if(userNameList.contains(SUPER_ADMINISTRATOR)){
                    /*
                      如果超级管理员 调assignAllPermit2SuperAdministrator
                    */
                    //查询超级管理员的rodeId
                    Long roleId = roleMapper.selectAllSuperAdministrator();
                    updateAffectedUserPermitCache.assignAllPermit2SuperAdministrator(roleId);
                    userAffectedMapper.deleteAffectedUserByUserName(SUPER_ADMINISTRATOR);
                    userNameList.remove(SUPER_ADMINISTRATOR);

                }
                // 更新角色下的用户的权限缓存
                permitStatBp.updateUserPermitCache(userNameList);
                logger.info("End updateUserPermitCache");
                //更新成功 删除更新了的用户
                userAffectedMapper.deleteAffectedUserByUserNameList(userNameList);
            }

        } catch (Exception e) {
            logger.error(String.format("updateUserPermitCache affectedUserList fail.affectedUserList:%s", StringUtility.toJSONString(userNames)),e);
        }

    }

}
