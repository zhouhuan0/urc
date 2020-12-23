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
import com.yks.urc.fw.DateUtil;
import com.yks.urc.fw.HttpUtility;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.hr.bp.api.IHrBp;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.mapper.IUserRoleMapper;
import com.yks.urc.permitStat.bp.api.IPermitRefreshTaskBp;
import com.yks.urc.vo.PositionVO;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.helper.VoHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    private String SynPositionPoint = "SYN_POSITION_POINT";
    private String URL = "GET_POSITION_URL";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void positionSync(boolean ifBymodifiedTime) throws Exception {
        Date date = new Date();
        String url = configBp.getString(URL, "http://ykshr.kokoerp.com/api/Position/getAllList");
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        String paramBody = null;
        if(ifBymodifiedTime){
            //获取同步时间点
            String modifiedTime = configBp.getString(SynPositionPoint, "2019-01-01 00:00:00");
            JSONObject data = new JSONObject();
            JSONObject object = new JSONObject();
            object.put("modifiedTime", DateUtil.String2Date(modifiedTime, DateUtil.YYYY_MM_DD_HH_MM_SS).getTime());
            data.put("data", object);
            paramBody = data.toJSONString();
        }

        String sendPost = HttpUtility.postHasHeaders(url, headMap, paramBody, "utf-8");
        logger.info("request position,url:{},response:{}", url, sendPost);
        if (StringUtility.isNullOrEmpty(sendPost)) {
            logger.error("同步岗位失败,人事系统接口无响应");
            return;
        }

        JSONObject jsonObject = StringUtility.parseString(sendPost);
        if (!StringUtility.stringEqualsIgnoreCase(jsonObject.getString("state"), CommonMessageCodeEnum.SUCCESS.getCode())) {
            logger.error("同步岗位失败,人事系统接报错", sendPost);
            return;
        }

        List<String> newUserName = new ArrayList<>();
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
            if (roleByRoleId == null) {
                addPosition(roleDO);
            } else {
                roleDO.setIsAuthorizable(roleByRoleId.getIsAuthorizable());
                roleDO.setRemark(roleByRoleId.getRemark());
                newUserName.addAll(updatePosition(roleDO, !StringUtility.stringEqualsIgnoreCaseObj(roleDO.isActive(), roleByRoleId.isActive())));
            }
        }

        if (!CollectionUtils.isEmpty(newUserName)) {
            //有变动的用户需要重新处理功能权限数据
            permitRefreshTaskBp.addPermitRefreshTask(newUserName.stream().distinct().collect(Collectors.toList()));
        }

        if(ifBymodifiedTime){
            //更新同步时间点
            configBp.update2Db(SynPositionPoint, DateUtil.formatDate(date, DateUtil.YYYY_MM_DD_HH_MM_SS));
        }
    }

    @Override
    public void asynPullPosition() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    hrBpr.positionSync(false);
                } catch (Exception e) {
                    logger.error("岗位数据同步出错:", e);
                }
            }
        }).start();
    }

    private void addPosition(RoleDO roleDO) throws Exception {
        int i = roleMapper.insertOrUpdate(roleDO);
        if (i > 0) {
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
            if (!CollectionUtils.isEmpty(lstUserName)) {
                /*批量新增用户-角色关系数据*/
                userRoleMapper.insertBatch(userRoleDOS);
            }
        }
    }

    /**
     *
     * @param roleDO
     * @param statusChange 岗位状态
     * @return
     * @throws Exception
     */
    private List<String> updatePosition(RoleDO roleDO,boolean statusChange) throws Exception {
        int i = roleMapper.updateByRoleId(roleDO);
        List<String> arrayList = new ArrayList<>();
        if (i > 0) {
            UserRoleDO ur = new UserRoleDO();
            ur.setRoleId(roleDO.getRoleId());
            //查询之前已有的用户-岗位关系数据
            List<String> oldUserList = userRoleMapper.getUserNameByRoleId(ur);
            List<String> copyOldUserList = new ArrayList<>(oldUserList);
            List<String> newUserName = getUserByPosition(roleDO.getRoleId());
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
            if (!CollectionUtils.isEmpty(userRoleDOS)) {
                /*批量添加用户-岗位关系数据*/
                userRoleMapper.insertBatch(userRoleDOS);
            }
            //相比之前要删除的用户
            copyOldUserList.removeAll(copyNewUserName);
            //如果岗位状态没变化只需要更新新增和删除人员的功能权限
            if(!statusChange){
                //相比之前新增的用户
                newUserName.removeAll(oldUserList);
            }
            newUserName.addAll(copyOldUserList);
            if (!CollectionUtils.isEmpty(newUserName)) {
                arrayList.addAll(copyOldUserList);
            }
        }
        return arrayList;
    }

    /**
     * 根据岗位ID请求岗位下的用户
     *
     * @param positionId
     * @return
     * @throws Exception
     */
    private List<String> getUserByPosition(Long positionId) throws Exception {
        String url = configBp.getString("GET_USERNAME_BY_POSITIONID_URL", "http://ykshr.kokoerp.com/api/Position/getUserNameByPositionId");
        Map<String, String> headMap = new HashMap<>();
        headMap.put("Content-Type", "application/json");
        JSONObject data = new JSONObject();
        JSONObject object = new JSONObject();
        object.put("positionId", positionId);
        data.put("data", object);
        String sendPost = HttpUtility.postHasHeaders(url, headMap, data.toJSONString(), "utf-8");
        logger.info("request getUserNameByPositionId,url:{},paramBody:{},response:{}", url, object.toJSONString(), sendPost);
        if (StringUtility.isNullOrEmpty(sendPost)) {
            logger.error("获取岗位用户关系失败,人事系统接口无响应");
            return Collections.emptyList();
        }

        JSONObject jsonObject = StringUtility.parseString(sendPost);
        if (!StringUtility.stringEqualsIgnoreCase(jsonObject.getString("state"), CommonMessageCodeEnum.SUCCESS.getCode())) {
            logger.error("获取岗位用户关系失败", sendPost);
            return Collections.emptyList();
        }
        List<String> list = StringUtility.jsonToList(jsonObject.getJSONObject("data").getString("list"), String.class);
        return CollectionUtils.isEmpty(list) ? Collections.EMPTY_LIST : list.stream().distinct().collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultVO updatePosition(String jsonStr) {
        String data = StringUtility.parseString(jsonStr).getString("data");
        if (StringUtility.isNullOrEmpty(data)) {
            return VoHelper.getSuccessResult();
        }
        List<PositionVO> list = StringUtility.jsonToList(data, PositionVO.class);
        List<String> newUserName = new ArrayList<>();
        for (PositionVO positionVO : list) {
            try {
                if (positionVO.getId() == null) {
                    continue;
                }
                RoleDO roleByRoleId = roleMapper.getRoleByRoleId(positionVO.getId().toString());
                //没有说明是新增的岗位,新增的岗位是没有任何功能权限直接做入库操作就好
                if (roleByRoleId == null) {
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
                    addPosition(roleDO);
                } else {
                    Boolean statusChange = !StringUtility.stringEqualsIgnoreCaseObj(positionVO.getStatus() == 1 ? Boolean.TRUE : Boolean.FALSE, roleByRoleId.isActive());
                    roleByRoleId.setPositionModifiedTime(positionVO.getModifiedTime());
                    roleByRoleId.setActive(positionVO.getStatus() == null ? Boolean.TRUE : (positionVO.getStatus() == 1 ? Boolean.TRUE : Boolean.FALSE));
                    roleByRoleId.setRoleName(positionVO.getName());
                    roleByRoleId.setModifiedTime(new Date());
                    newUserName.addAll(updatePosition(roleByRoleId, statusChange));
                }
            } catch (Exception e) {
                logger.error("updatePosition error;", StringUtility.toJSONString(positionVO));
            }
        }

        if (!CollectionUtils.isEmpty(newUserName)) {
            //有变动的用户需要重新处理功能权限数据
            permitRefreshTaskBp.addPermitRefreshTask(newUserName.stream().distinct().collect(Collectors.toList()));
        }
        return VoHelper.getSuccessResult();
    }
}
