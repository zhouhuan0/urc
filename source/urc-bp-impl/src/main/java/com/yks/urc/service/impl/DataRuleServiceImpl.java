package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.*;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.*;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 〈一句话功能简述〉
 * 功能权限模板操作service
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/12 9:04
 * @see DataRuleServiceImpl
 * @since JDK1.8
 */
@Service
public class DataRuleServiceImpl implements IDataRuleService {
    private static final Logger logger = Logger.getLogger(DataRuleServiceImpl.class);

    @Autowired
    private IDataRuleTemplMapper dataRuleTemplMapper;

    @Autowired
    private IDataRuleSysMapper dataRuleSysMapper;

    @Autowired
    private IDataRuleMapper dataRuleMapper;

    @Autowired
    private IMqBp mqBp;

    /**
     * Description: 根据模板Id获取数据权限模板
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/12 9:04
     * @see
     */
    @Override
    public ResultVO<DataRuleTemplVO> getDataRuleTemplByTemplId(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String createBy = jsonObject.get("operator").toString();
        Long templId = Long.valueOf(jsonObject.get("templId").toString());
        ResultVO resultVO = new ResultVO();
        DataRuleTemplVO dataRuleTemplVO = new DataRuleTemplVO();
        /**
         * 1、获取权限模板信息
         */
        Long tempId = Long.valueOf(templId);
        DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(tempId);
        BeanUtils.copyProperties(dataRuleTemplDO, dataRuleTemplVO);
        resultVO.data = dataRuleTemplVO;
        List<DataRuleSysDO> dataRuleSysDOS = dataRuleSysMapper.listByDataRuleId(tempId);
        List<DataRuleSysVO> dataRuleSysVOS = new ArrayList<>();
        for (DataRuleSysDO dataRuleSysDO : dataRuleSysDOS) {
            DataRuleSysVO dataRuleSysVO = new DataRuleSysVO();
            BeanUtils.copyProperties(dataRuleSysDO, dataRuleSysVO);
            dataRuleSysVOS.add(dataRuleSysVO);
        }
        dataRuleTemplVO.lstDataRuleSys = dataRuleSysVOS;
        return VoHelper.getSuccessResult(dataRuleTemplVO);
    }

    /**
     * Description: 根据方案名称、创建人等条件获取数据授权方案（分页）
     *
     * @param : jsonStr
     * @return: ResultVO<PageResultVO>
     * @auther: lvcr
     * @date: 2018/6/12 20:54
     * @see
     */
    @Override
    public ResultVO<PageResultVO> getDataRuleTempl(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        Map<String, Object> queryMap = new HashMap<>();
        Boolean rtn = checkAndConvertParam(queryMap, jsonObject);
        if (!rtn) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        List<DataRuleTemplDO> dataRuleTemplDOS = dataRuleTemplMapper.listDataRuleTemplDOsByPage(queryMap);
        List<DataRuleTemplVO> dataRuleTemplVOS = convertDoToVO(dataRuleTemplDOS);
        Long total = dataRuleTemplMapper.getCounts(queryMap.get("createBy").toString());
        PageResultVO pageResultVO = new PageResultVO(dataRuleTemplVOS, total, Integer.valueOf(queryMap.get("pageSize").toString()));
        return VoHelper.getSuccessResult(pageResultVO);
    }

    /**
     * Description: 检查输入参数并转换
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/13 11:54
     * @see
     */
    private Boolean checkAndConvertParam(Map<String, Object> queryMap, JSONObject jsonObject) {
         /*获取当前用户*/
        String createBy = jsonObject.get("operator").toString();
        if (StringUtility.isNullOrEmpty(createBy)) {
            logger.error("当期用户为空");
            return Boolean.FALSE;
        }
        queryMap.put("createBy", createBy);
        String pageNumber = jsonObject.get("pageNumber").toString();
        String pageData = jsonObject.get("pageData").toString();
        if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
            logger.error("分页参数有误");
            return Boolean.FALSE;
        }
        int currPage = Integer.valueOf(pageNumber);
        int pageSize = Integer.valueOf(pageData);
        queryMap.put("currIndex", (currPage - 1) * pageSize);
        queryMap.put("pageSize", pageSize);

        /*获取当前用户的角色*/
        RoleDO roleDO = new RoleDO();
        if (roleDO.isAuthorizable()) {
            //如果是管理员,createBy不作为查询条件
            queryMap.put("createBy", null);
        }
        /*获取模板名称*/
        DataRuleTemplVO dataRuleTemplVO = StringUtility.parseObject(jsonObject.get("templ").toString(), DataRuleTemplVO.class);

        String[] templNames = dataRuleTemplVO.templName.split("/r/n");
        queryMap.put("templNames", templNames);

        return Boolean.TRUE;


    }

    /**
     * Description: 数据授权方案-快速分配 -发送到MQ
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/12 21:03
     * @see
     */
    @Override
    public ResultVO assignDataRuleTempl2User(String jsonStr) {
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        String createBy = jsonObject.get("operator").toString();
        Long templId = Long.valueOf(jsonObject.get("templId").toString());
        List<String> lstUserName = StringUtility.jsonToList(jsonObject.get("lstUserName").toString(), String.class);
        List<DataRuleDO> dataRuleDOS = new ArrayList<>();
        for (String userName : lstUserName) {
            DataRuleDO dataRuleDO = new DataRuleDO();
            dataRuleDO.setCreateBy(createBy);
            dataRuleDO.setCreateTime(new Date());
            dataRuleDO.setUserName(userName);
            dataRuleDO.setDataRuleId(templId);
        }
        dataRuleMapper.insertBatch(dataRuleDOS);

        /*发送消息到kafka*/
        List<DataRuleSysDO> dataRuleSysDOS = dataRuleSysMapper.listByDataRuleId(templId);
        List<DataRuleSysVO> dataRuleSysVOS = new ArrayList<>();
        for (DataRuleSysDO dataRuleSysDO : dataRuleSysDOS) {
            DataRuleSysVO dataRuleSysVO = new DataRuleSysVO();
            BeanUtils.copyProperties(dataRuleSysDO, dataRuleSysVO);
            dataRuleSysVOS.add(dataRuleSysVO);
        }

        List<DataRuleVO> dataRuleVOS = new ArrayList<>();
        for (String userName : lstUserName) {
            DataRuleVO dataRuleVO = new DataRuleVO();
            dataRuleVO.userName = userName;
            dataRuleVO.lstDataRuleSys = dataRuleSysVOS;
            dataRuleVOS.add(dataRuleVO);
        }
        mqBp.send2Mq(dataRuleVOS);
        return VoHelper.getSuccessResult();
    }

    private List<DataRuleTemplVO> convertDoToVO(List<DataRuleTemplDO> dataRuleTemplDOS) {
        List<DataRuleTemplVO> dataRuleTemplVOS = new ArrayList<>();
        for (DataRuleTemplDO dataRuleTemplDO : dataRuleTemplDOS) {
            DataRuleTemplVO dataRuleTemplVO = new DataRuleTemplVO();
            BeanUtils.copyProperties(dataRuleTemplDO, dataRuleTemplVO);
            dataRuleTemplVOS.add(dataRuleTemplVO);
        }
        return dataRuleTemplVOS;
    }

}
