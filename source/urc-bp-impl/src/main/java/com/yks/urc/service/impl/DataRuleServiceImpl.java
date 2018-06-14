package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.*;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.*;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ISeqBp seqBp;

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
        String operator = jsonObject.get("operator").toString();
        Long templId = Long.valueOf(jsonObject.get("templId").toString());
        ResultVO resultVO = new ResultVO();
        DataRuleTemplVO dataRuleTemplVO = new DataRuleTemplVO();
        /**
         * 1、获取权限模板信息
         */
        Long tempId = Long.valueOf(templId);
        String createBy = operator;
        DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(templId, createBy);
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
        /*1、json字符串转json对象*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、请求参数的基本校验并转换为内部使用的Map*/
        Map<String, Object> queryMap = new HashMap<>();
        Boolean rtn = checkAndConvertParam(queryMap, jsonObject);
        if (!rtn) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        /*3、查询数据群贤模板列表信息*/
        List<DataRuleTemplDO> dataRuleTemplDOS = dataRuleTemplMapper.listDataRuleTemplDOsByPage(queryMap);
        /*4、List<DO> 转 List<VO>*/
        List<DataRuleTemplVO> dataRuleTemplVOS = convertDoToVO(dataRuleTemplDOS);
        /*5、获取总条数*/
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
        String createBy = jsonObject.getString("operator");
        if (StringUtility.isNullOrEmpty(createBy)) {
            logger.error("当期用户为空");
            return Boolean.FALSE;
        }
        queryMap.put("createBy", createBy);
        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
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
        /*获取复数模板名称*/
        JSONObject templJson = jsonObject.getJSONObject("templ");
        if (templJson == null) {
            logger.error("分页参数 templ 有误");
            return Boolean.FALSE;
        }
        DataRuleTemplVO dataRuleTemplVO = StringUtility.parseObject(templJson.toString(), DataRuleTemplVO.class);
        String[] templNames = dataRuleTemplVO.templName.split(System.getProperty("line.separator"));
        queryMap.put("templNames", templNames);
        return Boolean.TRUE;


    }


    /**
     * Description: 数据授权方案1-快速分配   2-发送到MQ
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/12 21:03
     * @see
     */
    @Transactional
    @Override
    public ResultVO assignDataRuleTempl2User(String jsonStr) {
        /*1、将json字符串转为Json对象*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、获取参数并校验*/
        String createBy = jsonObject.getString("operator");
        if (StringUtil.isEmpty(createBy)) {
            logger.error("当前用户为空");
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        String templIdStr = jsonObject.getString("templId");
        if (!StringUtil.isNum(templIdStr)) {
            logger.error("参数有误");
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        Long templId = Long.valueOf(templIdStr);
        String lstUserNameStr = jsonObject.getString("lstUserName");
        if (StringUtil.isEmpty(templIdStr)) {
            logger.error("参数有误");
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        List<String> lstUserName = StringUtility.jsonToList(lstUserNameStr, String.class);
        if (lstUserName == null || lstUserName.isEmpty()) {
            logger.error("参数有误");
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
        }
        /*3、批量添加用户-数据权限关系到数据库*/
        List<DataRuleDO> dataRuleDOS = new ArrayList<>();
        for (String userName : lstUserName) {
            DataRuleDO dataRuleDO = new DataRuleDO();
            dataRuleDO.setCreateBy(createBy);
            dataRuleDO.setCreateTime(new Date());
            dataRuleDO.setUserName(userName);
            dataRuleDO.setDataRuleId(templId);
            dataRuleDOS.add(dataRuleDO);
        }
        dataRuleMapper.insertBatch(dataRuleDOS);

        /*4、发送消息到kafka*/
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

    /**
     * Description:新增或编辑一个方案
     *
     * @param :jsonStr
     * @return: ResultVO<DataRuleTemplVO>
     * @auther: lvcr
     * @date: 2018/6/13 15:06
     * @see
     */
    @Transactional
    @Override
    public ResultVO<DataRuleTemplVO> addOrUpdateDataRuleTempl(String jsonStr) {
        /*1、将json字符串转为Json对象*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、获取参数*/
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        DataRuleTemplVO templVO = StringUtility.parseObject(jsonObject.getString("templ"), DataRuleTemplVO.class);
        if (templVO == null) {
            return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_NULL.getCode(), CommonMessageCodeEnum.PARAM_NULL.getDesc());
        }
        /** 3、判断该方案是否属于当前用户（非管理员角色）
         *  1)、当temp方案存在时候， 2)、当前用户非管理员  3)、temp方案不属于当前用户
         */
        String tempIdStr = templVO.getTemplId();
        if(!StringUtil.isEmpty(tempIdStr)){
            DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(Long.valueOf(tempIdStr),operator);
            if(dataRuleTemplDO == null){
                logger.error("该方案不属于该用户，不能操作");
                return VoHelper.getErrorResult(CommonMessageCodeEnum.PARAM_INVALID.getCode(), CommonMessageCodeEnum.PARAM_INVALID.getDesc());
            }
        }

        /*4、删除该方案对应的数据(包括对应的数据权限)*/
        Long currentTemplId = Long.valueOf(tempIdStr);
        deleteDataRuleTempl(currentTemplId);


        /*3、新增数据权限模板  urc_data_rule_templ记录*/
        DataRuleTemplDO dataRuleTemplDO = new DataRuleTemplDO();
        BeanUtils.copyProperties(templVO, dataRuleTemplDO);
        dataRuleTemplDO.setCreateBy(operator);
        dataRuleTemplDO.setCreateTime(new Date());
        Long templId = seqBp.getNextDataRuleTemplId();
        dataRuleTemplDO.setTemplId(templId);
        dataRuleTemplMapper.insert(dataRuleTemplDO);
        /*4、操作数据权限系统 urc_data_rule_sys*/
        List<DataRuleSysVO> dataRuleSysVOList = templVO.getLstDataRuleSys();
        List<DataRuleSysDO> dataRuleSysDOS = new ArrayList<>();
        for (DataRuleSysVO dataRuleSysVO : dataRuleSysVOList) {
            DataRuleSysDO dataRuleSysDO = new DataRuleSysDO();
            BeanUtils.copyProperties(dataRuleSysVO, dataRuleSysDO);
            dataRuleSysDO.setDataRuleId(templId);
            Long dataRuleSysId = seqBp.getNextDataRuleSysId();
            dataRuleSysDO.setDataRuleSysId(dataRuleSysId);
            dataRuleSysDO.setCreateBy(operator);
            dataRuleSysDO.setCreateTime(new Date());
            dataRuleSysDOS.add(dataRuleSysDO);
            /*5、操作urc_sql数据*/
            // List<UrcSqlVO> urcSqlVOS = dataRuleSysVO.urcSqlDOList;
        }


        return null;
    }

    private void deleteDataRuleTempl(Long templId) {
//        dataRuleTemplMapper

    }

    /**
     * Description: List<DO> 转List<VO>
     *
     * @param : dataRuleTemplDOS
     * @return: List<DataRuleTemplVO>
     * @auther: lvcr
     * @date: 2018/6/13 13:00
     * @see
     */
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
