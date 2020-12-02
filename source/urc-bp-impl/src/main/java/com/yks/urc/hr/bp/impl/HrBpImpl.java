/*
 * 文件名：HrBpImpl.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：
 * 创建人：zhouhuan
 * 创建时间：2020/11/27
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.hr.bp.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.config.bp.api.IConfigBp;
import com.yks.urc.constant.UrcConstant;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.entity.UserRoleDO;
import com.yks.urc.enums.CommonMessageCodeEnum;
import com.yks.urc.fw.BeanProvider;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.hr.bp.api.IHrBp;
import com.yks.urc.lock.bp.api.ILockBp;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.session.bp.api.ISessionBp;
import com.yks.urc.vo.PositionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author zhouhuan
 * @version 1.0
 * @date 2020/11/27
 * @see HrBpImpl
 * @since JDK1.8
 */
@Component
public class HrBpImpl implements IHrBp {
    private static Logger logger = LoggerFactory.getLogger(HrBpImpl.class);

    @Autowired
    private IConfigBp configBp;
    @Autowired
    private IRoleMapper roleMapper;
    @Autowired
    private IHrBp hrBpr;
    @Autowired
    private IUserRoleMapper userRoleMapper;
    @Autowired
    private IPermitRefreshTaskBp permitRefreshTaskBp;
    @Autowired
    private ILockBp lockBp;
    private String synPosition = "synPosition";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void positionSync() throws Exception {
        if(!lockBp.tryLock(synPosition)){
            return;
        }
        try {
            String url = configBp.getString("GET_POSITION_URL", "http://ykshr.kokoerp.com/api/Position/getAllList");
            String sendPost = HttpUtility.sendPost(url, null);
            logger.info("request position,url:{},response:{}",url,sendPost);
            if (StringUtility.isNullOrEmpty(sendPost)) {
                logger.error("同步岗位失败,人事系统接口无响应");
                return;
            }

            JSONObject jsonObject = StringUtility.parseString(sendPost);
            if(!StringUtility.stringEqualsIgnoreCase(jsonObject.getString("state"),CommonMessageCodeEnum.SUCCESS.getCode())){
                logger.error("同步岗位失败,人事系统接报错",sendPost);
                return;
            }

            List<PositionVO> list = StringUtility.jsonToList(jsonObject.getJSONObject("data").getString("list"), PositionVO.class);
            for (PositionVO positionVO : list) {
                RoleDO roleDO = new RoleDO();
                roleDO.setRoleType(UrcConstant.RoleType.position);
                roleDO.setActive(positionVO.getStatus() == null ? Boolean.TRUE : (positionVO.getStatus() == 1 ? Boolean.TRUE : Boolean.FALSE));
                roleDO.setIsAuthorizable(0);
                roleDO.setCreateBy(positionVO.getOperator());
                roleDO.setForever(Boolean.TRUE);
                roleDO.setModifiedBy(positionVO.getOperator());
                roleDO.setCreateTime(positionVO.getCreateTime());
                roleDO.setModifiedTime(new Date());
                roleDO.setRoleId(positionVO.getId());
                roleDO.setPositionModifiedTime(positionVO.getModifiedTime());
                roleDO.setRoleName(positionVO.getName());
                RoleDO roleByRoleId = roleMapper.getRoleByRoleId(positionVO.getId().toString());
                //没有说明是新增的岗位,新增的岗位是没有任何功能权限直接做入库操作就好
                if(roleByRoleId == null){
                    addPosition(roleDO);
                }else {
                    roleDO.setIsAuthorizable(roleByRoleId.getIsAuthorizable());
                    roleDO.setRemark(roleByRoleId.getRemark());
                    updatePosition(roleDO);
                }
            }
        } finally {
            lockBp.unlock(synPosition);
        }
    }

    @Override
    public void asynPullPosition() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hrBpr.positionSync();
                } catch (Exception e) {
                    logger.error("岗位数据同步出错:", e);
                }
            }
        }).start();
    }

    private void addPosition(RoleDO roleDO) throws Exception {
        int i = roleMapper.insertOrUpdate(roleDO);
        if(i>0){
            List<String> lstUserName = getUserByPosition(roleDO.getId());
            List<UserRoleDO> userRoleDOS = new ArrayList<>();
            for (String userName : lstUserName) {
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserName(userName);
                userRoleDO.setRoleId(roleDO.getRoleId());
                userRoleDO.setCreateBy(roleDO.getCreateBy());
                userRoleDO.setCreateTime(new Date());
                userRoleDO.setModifiedBy(roleDO.getCreateBy());
                userRoleDO.setModifiedTime(new Date());
                userRoleDOS.add(userRoleDO);
            }
            /*删除原有用户-角色关系数据*/
            userRoleMapper.deleteByRoleId(roleDO.getRoleId());
            if (!CollectionUtils.isEmpty(lstUserName)){
                /*批量新增用户-角色关系数据*/
                userRoleMapper.insertBatch(userRoleDOS);
            }
        }
    }

    private void updatePosition(RoleDO roleDO)throws Exception{
        int i = roleMapper.insertOrUpdate(roleDO);
        if(i>0){
            UserRoleDO ur = new UserRoleDO();
            ur.setRoleId(roleDO.getRoleId());
            //查询之前已有的用户-岗位关系数据
            List<String> oldUserList= userRoleMapper.getUserNameByRoleId(ur);
            List<String> copyOldUserList = new ArrayList<>(oldUserList);
            List<String> newUserName = getUserByPosition(roleDO.getId());
            List<String> copyNewUserName = new ArrayList<>(newUserName);
            List<UserRoleDO> userRoleDOS = new ArrayList<>();
            for (String userName : newUserName) {
                UserRoleDO userRoleDO = new UserRoleDO();
                userRoleDO.setUserName(userName);
                userRoleDO.setRoleId(roleDO.getRoleId());
                userRoleDO.setCreateBy(roleDO.getCreateBy());
                userRoleDO.setCreateTime(new Date());
                userRoleDO.setModifiedBy(roleDO.getCreateBy());
                userRoleDO.setModifiedTime(new Date());
                userRoleDOS.add(userRoleDO);
            }

            /*删除原有用户-岗位关系数据*/
            userRoleMapper.deleteByRoleId(roleDO.getRoleId());
            if (!CollectionUtils.isEmpty(userRoleDOS)){
                /*批量添加用户-岗位关系数据*/
                userRoleMapper.insertBatch(userRoleDOS);
            }

            //相比之前新增的用户
            newUserName.removeAll(oldUserList);
            //相比之前要删除的用户
            copyOldUserList.removeAll(copyNewUserName);
            newUserName.addAll(copyOldUserList);
            if(!CollectionUtils.isEmpty(newUserName)){
                //有变动的用户需要重新处理功能权限数据
                permitRefreshTaskBp.addPermitRefreshTask(newUserName);
            }
        }
    }

    /**
     * 根据岗位ID请求岗位下的用户
     * @param positionId
     * @return
     * @throws Exception
     */
    private List<String> getUserByPosition(Long positionId) throws Exception {
        String url = configBp.getString("GET_USERNAME_BY_POSITIONID_URL", "http://ykshr.kokoerp.com/api/Position/getUserNameByPositionId");
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        JSONObject object = new JSONObject();
        object.put("positionId", positionId);
        String sendPost = HttpUtility.postHasHeaders(url,headMap, object.toJSONString(), "utf-8");
        logger.info("request getUserNameByPositionId,url:{},paramBody:{},response:{}",url,object.toJSONString(),sendPost);
        if (StringUtility.isNullOrEmpty(sendPost)) {
            logger.error("获取岗位用户关系失败,人事系统接口无响应");
            return Collections.emptyList();
        }

        JSONObject jsonObject = StringUtility.parseString(sendPost);
        if(!StringUtility.stringEqualsIgnoreCase(jsonObject.getString("state"),CommonMessageCodeEnum.SUCCESS.getCode())){
            logger.error("获取岗位用户关系失败",sendPost);
            return Collections.emptyList();
        }
       return StringUtility.jsonToList(jsonObject.getJSONObject("data").getString("list"), String.class);
    }
}
