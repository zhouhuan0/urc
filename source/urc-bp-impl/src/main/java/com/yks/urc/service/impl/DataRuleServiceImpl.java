package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yks.common.enums.CommonMessageCodeEnum;
import com.yks.common.util.DateUtil;
import com.yks.common.util.StringUtil;
import com.yks.urc.entity.*;
import com.yks.urc.exception.ErrorCode;
import com.yks.urc.exception.URCBizException;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.*;
import com.yks.urc.mq.bp.api.IMqBp;
import com.yks.urc.seq.bp.api.ISeqBp;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.vo.*;
import com.yks.urc.vo.helper.Query;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.h2.engine.User;
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
    private IDataRuleMapper dataRuleMapper;

    @Autowired
    private IDataRuleSysMapper dataRuleSysMapper;

    @Autowired
    private IUserRoleMapper userRoleMapper;

    @Autowired
    private IRoleMapper roleMapper;

    @Autowired
    private IExpressionMapper expressionMapper;

    @Autowired
    private IDataRuleColMapper dataRuleColMapper;

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
        /**
         * 1、获取参数并做基本校验
         */
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            // logger.error("当前用户不能为空");
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        String templIdStr = jsonObject.getString("templId");
        if (StringUtil.isEmpty(templIdStr)) {
            throw new URCBizException("parameter templId is null", ErrorCode.E_000002);
        }
        Long templId = Long.valueOf(templIdStr);
        DataRuleTemplVO dataRuleTemplVO = new DataRuleTemplVO();
        /**
         * 2、获取权限模板信息
         */
        DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(templId, operator);
        if (dataRuleTemplDO == null) {
            return VoHelper.getSuccessResult();
            //throw new URCBizException(String.format("urc_data_rule_templ is null where templId is: %s and operator is: %s",templId,operator),ErrorCode.E_000002);
        }
        BeanUtils.copyProperties(dataRuleTemplDO, dataRuleTemplVO);
        dataRuleTemplVO.setTemplId(String.valueOf(dataRuleTemplDO.getTemplId()));

        /**
         *3、获取数据权限Sys  行权限数据 列权限数据
         */
        List<DataRuleSysDO> dataRuleSysDOS = dataRuleSysMapper.getDataRuleSysDatas(templId);
        /**
         * 4、重新组装行权限数据
         */
        for (DataRuleSysDO dataRuleSysDO : dataRuleSysDOS) {
            List<ExpressionDO> expressionDOS = dataRuleSysDO.getExpressionDOS();
            /*获取父级行权限*/
            ExpressionDO parentExpressionDO = null;
            for (ExpressionDO expressionDO : expressionDOS) {
                if (expressionDO.getParentExpressionId() == null) {
                    parentExpressionDO = expressionDO;
                    break;
                }
            }
            /*组装父级行权限*/
            List<ExpressionDO> subExpressionDOList = new ArrayList<>();
            for (ExpressionDO expressionDO : expressionDOS) {
                if (expressionDO.getParentExpressionId() != null && parentExpressionDO.getExpressionId().equals(expressionDO.getParentExpressionId())) {
                    subExpressionDOList.add(expressionDO);
                }
                parentExpressionDO.setExpressionDOList(subExpressionDOList);
            }
            dataRuleSysDO.setParentExpressionDO(parentExpressionDO);
        }
        /**
         * 5、DO 转 VO
         */

        List<DataRuleSysVO> dataRuleSysVOS = new ArrayList<>();
        for (DataRuleSysDO dataRuleSysDO : dataRuleSysDOS) {
            DataRuleSysVO dataRuleSysVO = new DataRuleSysVO();
            BeanUtils.copyProperties(dataRuleSysDO, dataRuleSysVO);
            /*列权限DO 转 VO*/
            List<DataRuleColVO> dataRuleColVOS = new ArrayList<>();
            List<DataRuleColDO> dataRuleColDOS = dataRuleSysDO.getDataRuleColDOS();
            for (DataRuleColDO dataRuleColDO : dataRuleColDOS) {
                DataRuleColVO dataRuleColVO = new DataRuleColVO();
                BeanUtils.copyProperties(dataRuleColDO, dataRuleColVO);
                dataRuleColVOS.add(dataRuleColVO);

                dataRuleSysVO.setCol(dataRuleColVOS);
            }
            /*行权限 DO 转 VO*/
            ExpressionDO parentExpressionDO = dataRuleSysDO.getParentExpressionDO();
            ExpressionVO parentExpressionVO = new ExpressionVO();
            BeanUtils.copyProperties(parentExpressionDO, parentExpressionVO);
            parentExpressionVO.setIsAnd(parentExpressionDO.getAnd() ? 1 : 0);
            List<ExpressionDO> subExpressionDOS = parentExpressionDO.getExpressionDOList();
            List<ExpressionVO> subExpressionVOS = new ArrayList<>();
            for (ExpressionDO subExpressionDO : subExpressionDOS) {
                ExpressionVO subExpressionVO = new ExpressionVO();
                BeanUtils.copyProperties(subExpressionDO, subExpressionVO);
                subExpressionVO.setOperValuesArr(JSONArray.parseArray(subExpressionDO.getOperValues(),String.class));
                subExpressionVOS.add(subExpressionVO);
            }
            parentExpressionVO.setSubWhereClause(subExpressionVOS);
            dataRuleSysVO.setRow(parentExpressionVO);

            dataRuleSysVOS.add(dataRuleSysVO);
        }
        /*设置权限系统数据  一个系统一个dataRukeSysVo*/
        dataRuleTemplVO.setLstDataRuleSys(dataRuleSysVOS);

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
        checkAndConvertParam(queryMap, jsonObject);
        /*3、查询数据权限模板列表信息*/
        List<DataRuleTemplDO> dataRuleTemplDOS = dataRuleTemplMapper.listDataRuleTemplDOsByPage(queryMap);
        /*4、List<DO> 转 List<VO>*/
        List<DataRuleTemplVO> dataRuleTemplVOS = convertDoToVO(dataRuleTemplDOS);
        /*5、获取总条数*/
        Long total = dataRuleTemplMapper.getCounts(queryMap);
        PageResultVO pageResultVO = new PageResultVO(dataRuleTemplVOS, total, queryMap.get("pageSize").toString());
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
    private void checkAndConvertParam(Map<String, Object> queryMap, JSONObject jsonObject) {
         /*获取当前用户*/
        String operator = jsonObject.getString("operator");
        if (StringUtility.isNullOrEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        queryMap.put("createBy", operator);
        String pageNumber = jsonObject.getString("pageNumber");
        String pageData = jsonObject.getString("pageData");
        if (!StringUtil.isNum(pageNumber) || !StringUtil.isNum(pageData)) {
            throw new URCBizException(String.format("parameter pageNumber or pageData is not num pageNumber:%s , pageData:%s", pageNumber, pageData), ErrorCode.E_000002);
        }
        int currPage = Integer.valueOf(pageNumber);
        int pageSize = Integer.valueOf(pageData);
        queryMap.put("currIndex", (currPage - 1) * pageSize);
        queryMap.put("pageSize", pageSize);

        /*获取当前用户的角色*/
        Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        if (isAdmin) {
            //如果是管理员,createBy不作为查询条件
            queryMap.put("createBy", null);
        }
        /*获取复数模板名称*/
        String templStr = jsonObject.getString("templ");
        if (StringUtil.isNotEmpty(templStr)) {
            DataRuleTemplVO dataRuleTemplVO = StringUtility.parseObject(templStr, DataRuleTemplVO.class);
            String[] templNames = dataRuleTemplVO.templName.split(System.getProperty("line.separator"));
            queryMap.put("templNames", templNames);
        }
    }


    /**
     * Description: 数据授权方案 快速分配数据权限模板给用户
     * 1-快速分配
     * 2-发送到MQ
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
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        String templIdStr = jsonObject.getString("templId");
        if (StringUtil.isEmpty(templIdStr)) {
            throw new URCBizException("parameter templId is null", ErrorCode.E_000002);
        }
        Long templId = Long.valueOf(templIdStr);
        String lstUserNameStr = jsonObject.getString("lstUserName");
        if (StringUtil.isEmpty(lstUserNameStr)) {
            throw new URCBizException("parameter lstUserName is null", ErrorCode.E_000002);
        }
        List<String> lstUserName = StringUtility.jsonToList(lstUserNameStr, String.class);
        if (lstUserName == null || lstUserName.isEmpty()) {
            throw new URCBizException("parameter lstUserName is null", ErrorCode.E_000002);
        }
        /*3、获取该模板对应的数据权限对应系统数据*/
        List<DataRuleSysDO> dataRuleSysDOS = dataRuleSysMapper.getDataRuleSysDatas(templId);
        if (dataRuleSysDOS == null || dataRuleSysDOS.isEmpty()) {
            throw new URCBizException("get urc_data_rule_sys is null where templId is:" + templId, ErrorCode.E_000003);
        }
        /*数据权限对应系统缓存列表 */
        List<DataRuleSysDO> dataRuleSysDOSCache = new ArrayList<>();
        /*用户-数据权限关系缓存列表*/
        List<DataRuleDO> dataRuleDOSCache = new ArrayList<>();
         /*列权限数据缓存列表*/
        List<DataRuleColDO> dataRuleColDOSCache = new ArrayList<>();
         /*行权限数据缓存列表*/
        List<ExpressionDO> expressionDOSCache = new ArrayList<>();
       /*4、组装数据  并放入待入库列表*/
        assembleDatasToAdd(dataRuleSysDOSCache, dataRuleDOSCache, dataRuleColDOSCache, expressionDOSCache, lstUserName, createBy, dataRuleSysDOS);
        /*5、删除用户原有的数据权限关系数据 包括行权限 列权限*/
        dataRuleMapper.delBatchByUserNames(lstUserName);
        /*6 批量添加用户数据权限关系数据*/
        if(dataRuleDOSCache!=null && !dataRuleDOSCache.isEmpty()){
            dataRuleMapper.insertBatch(dataRuleDOSCache);
        }
        /*7 批量添加数据权限系统数据*/
        if(dataRuleSysDOSCache!=null && !dataRuleSysDOSCache.isEmpty()) {
            dataRuleSysMapper.insertBatch(dataRuleSysDOSCache);
        }
        /*批量添加对应的行权限数据*/
        if(dataRuleColDOSCache!=null && !dataRuleColDOSCache.isEmpty()) {
            dataRuleColMapper.insertBatch(dataRuleColDOSCache);
        }
        /*批量添加对应的列权限数据*/
        if(expressionDOSCache!=null && !expressionDOSCache.isEmpty()) {
            expressionMapper.insertBatch(expressionDOSCache);
        }
        /*8、发送消息到kafka*/
        sendToMq(dataRuleSysDOS, lstUserName);
        return VoHelper.getSuccessResult();
    }

    /**
     * Description: 组装数据  并放入待入库列表
     *
     * @param :
     * @param dataRuleColDOSCache
     * @param expressionDOSCache  @return:
     * @auther: lvcr
     * @date: 2018/6/15 14:29
     * @see
     */
    private void assembleDatasToAdd(List<DataRuleSysDO> dataRuleSysDOSCache,
                                    List<DataRuleDO> dataRuleDOSCache,
                                    List<DataRuleColDO> dataRuleColDOSCache,
                                    List<ExpressionDO> expressionDOSCache,
                                    List<String> lstUserName,
                                    String createBy,
                                    List<DataRuleSysDO> dataRuleSysDOS) {
        for (String userName : lstUserName) {
            /*4、组装用户-数据权限关系数据  并放入待入库列表*/
            DataRuleDO dataRuleDO = new DataRuleDO();
            dataRuleDO.setCreateBy(createBy);
            dataRuleDO.setCreateTime(new Date());
            dataRuleDO.setUserName(userName);
            Long dataRuleId = seqBp.getNextDataRuleId();
            dataRuleDO.setDataRuleId(dataRuleId);
            dataRuleDOSCache.add(dataRuleDO);
            for (DataRuleSysDO dataRuleSysDO : dataRuleSysDOS) {
                DataRuleSysDO targetDataRuleSysDO = new DataRuleSysDO();
                BeanUtils.copyProperties(dataRuleSysDO, targetDataRuleSysDO);
                /*5、组装数据权限对应系统数据  并放入待入库缓存列表*/
                targetDataRuleSysDO.setDataRuleId(dataRuleId);
                Long dataRuleSysId = seqBp.getNextDataRuleSysId();
                targetDataRuleSysDO.setDataRuleSysId(dataRuleSysId);
                targetDataRuleSysDO.setCreateTime(new Date());
                targetDataRuleSysDO.setCreateBy(createBy);
                dataRuleSysDOSCache.add(targetDataRuleSysDO);
                /*6、组装列权限数据  并放入待入库缓存列表*/
                List<DataRuleColDO> dataRuleColDOS = dataRuleSysDO.getDataRuleColDOS();
                if (dataRuleColDOS != null && !dataRuleColDOS.isEmpty()) {
                    for (DataRuleColDO dataRuleColDO : dataRuleColDOS) {
                        DataRuleColDO targetDataRuleColDO = new DataRuleColDO();
                        BeanUtils.copyProperties(dataRuleColDO, targetDataRuleColDO);
                        targetDataRuleColDO.setDataRuleSysId(dataRuleSysId);
                        targetDataRuleColDO.setCreateBy(createBy);
                        targetDataRuleColDO.setCreateTime(new Date());
                        dataRuleColDOSCache.add(targetDataRuleColDO);
                    }
                }
                /*7、组装行权限数据  并放入待入库缓存列表*/
                List<ExpressionDO> expressionDOS = dataRuleSysDO.getExpressionDOS();
                Long parentExpressionId = null;
                /*父级行权限数据*/
                for (ExpressionDO expressionDO : expressionDOS) {
                    if (expressionDO.getParentExpressionId() == null) {
                        ExpressionDO targetExpressionDO = new ExpressionDO();
                        BeanUtils.copyProperties(expressionDO, targetExpressionDO);
                        targetExpressionDO.setDataRuleSysId(dataRuleSysId);
                        parentExpressionId = seqBp.getExpressionId();
                        targetExpressionDO.setExpressionId(parentExpressionId);
                        targetExpressionDO.setCreateBy(createBy);
                        targetExpressionDO.setCreateTime(new Date());
                        expressionDOSCache.add(targetExpressionDO);
                    }
                }
                /*子集行权限数据*/
                for (ExpressionDO expressionDO : expressionDOS) {
                    if (expressionDO.getParentExpressionId() != null) {
                        ExpressionDO targetExpressionDO = new ExpressionDO();
                        BeanUtils.copyProperties(expressionDO, targetExpressionDO);
                        targetExpressionDO.setDataRuleSysId(dataRuleSysId);
                        Long expressionId = seqBp.getExpressionId();
                        targetExpressionDO.setExpressionId(expressionId);
                        targetExpressionDO.setCreateBy(createBy);
                        targetExpressionDO.setCreateTime(new Date());
                        targetExpressionDO.setParentExpressionId(parentExpressionId);
                        expressionDOSCache.add(targetExpressionDO);
                    }
                }
            }
        }
    }

    /**
     * Description: 发送消息到MQ
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 14:26
     * @see
     */
    private void sendToMq(List<DataRuleSysDO> dataRuleSysDOS, List<String> lstUserName) {
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
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        DataRuleTemplVO templVO = jsonObject.getObject("templ",DataRuleTemplVO.class);
        /*校验基本参数*/
        checkParam(templVO);
        /** 3、判断该方案是否属于当前用户（非管理员角色）
         *  1)、当temp方案存在 2)、当前用户非管理员  3)、temp方案不属于当前用户
         */
        String tempIdStr = templVO.getTemplId();
        if (!StringUtil.isEmpty(tempIdStr)) {
            Long currentTemplId = Long.valueOf(tempIdStr);
            /*判断是否为超级管理员*/
            Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
            if(!isAdmin) {
                DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(currentTemplId, operator);
                if (dataRuleTemplDO == null) {
                    throw new URCBizException(String.format("该方案不属于该用户，不能操作 where templId is: %s and operator is: %s", currentTemplId, operator), ErrorCode.E_000003);
                }
            }
             /*4、删除该方案对应的数据(包括对应的数据权限Sys、行权限、列权限)*/
            dataRuleTemplMapper.delTemplDatasById(currentTemplId);
        }
        /*5、新增该方案对应的数据（包括对应的数据权限Sys、行权限、列权限）*/
        insertDataRuleTemlDatas(templVO, operator);
        return VoHelper.getSuccessResult(templVO);
    }

    private void checkParam(DataRuleTemplVO templVO) {
        if (templVO == null) {
            throw new URCBizException("parameter templ is null", ErrorCode.E_000002);
        }
        if(StringUtil.isEmpty(templVO.getTemplName())){
            throw new URCBizException("parameter templName is null", ErrorCode.E_000002);
        }
    }

    /**
     * Description:
     * 1、组装数据权限sys数据，并添加到缓存List dataRuleSysCache里，供批量添加
     * 2、组装行权限数据，并添加到缓存List expressionCache里，供批量添加
     * 3、组装列权限数据，并添加到缓存List dataRuleColCache里，供批量添加
     *
     * @param :dataRuleSysCache
     * @param :dataRuleColCache
     * @param :expressionCache
     * @param :dataRuleSysVOS
     * @param :templId          模板Id
     * @param :operator         当前用户
     * @auther: lvcr
     * @date: 2018/6/14 20:00
     * @see
     */
    private void assembleDataRuleSysDatas(List<DataRuleSysDO> dataRuleSysCache, List<DataRuleColDO> dataRuleColCache, List<ExpressionDO> expressionCache, List<DataRuleSysVO> dataRuleSysVOS, Long templId, String operator) {
        for (DataRuleSysVO dataRuleSysVO : dataRuleSysVOS) {
            /*1、添加到数据权限Sys列表*/
            DataRuleSysDO dataRuleSysDO = new DataRuleSysDO();
            BeanUtils.copyProperties(dataRuleSysVO, dataRuleSysDO);
            Long dataRuleSysId = seqBp.getNextDataRuleSysId();
            dataRuleSysDO.setDataRuleSysId(dataRuleSysId);
            dataRuleSysDO.setDataRuleId(templId);
            dataRuleSysDO.setCreateBy(operator);
            dataRuleSysDO.setCreateTime(new Date());
            dataRuleSysCache.add(dataRuleSysDO);
            /*2、添加到行权限数据列表*/
            /*获取行权限数据*/
            ExpressionVO parentExpressionVO = dataRuleSysVO.getRow();
            /*组装父级行权限*/
            ExpressionDO parentExpressionDO = new ExpressionDO();
            BeanUtils.copyProperties(parentExpressionVO, parentExpressionDO);
            parentExpressionDO.setDataRuleSysId(dataRuleSysId);
            Long parentExpressionId = seqBp.getExpressionId();
            parentExpressionDO.setExpressionId(parentExpressionId);
            parentExpressionDO.setParentExpressionId(null);
            parentExpressionDO.setAnd(parentExpressionVO.getIsAnd() == 1 ? true : false);
            parentExpressionDO.setFieldCode(null);
            parentExpressionDO.setCreateBy(operator);
            parentExpressionDO.setCreateTime(new Date());
            expressionCache.add(parentExpressionDO);
            /*组装子级行权限数据*/
            List<ExpressionVO> subExpressions = parentExpressionVO.getSubWhereClause();
            if (subExpressions != null && !subExpressions.isEmpty()) {
                for (ExpressionVO subExpressionVO : subExpressions) {
                    ExpressionDO subExpressionDO = new ExpressionDO();
                    BeanUtils.copyProperties(subExpressionVO, subExpressionDO);
                    List<String> operValuesArrList = subExpressionVO.getOperValuesArr();
                    if(operValuesArrList!=null && !operValuesArrList.isEmpty()){
                        subExpressionDO.setOperValues(StringUtility.toJSONString(subExpressionVO.getOperValuesArr()));
                    }
                    subExpressionDO.setDataRuleSysId(dataRuleSysId);
                    subExpressionDO.setParentExpressionId(parentExpressionId);
                    Long expressionId = seqBp.getExpressionId();
                    subExpressionDO.setExpressionId(expressionId);
                    subExpressionDO.setCreateTime(new Date());
                    subExpressionDO.setCreateBy(operator);
                    expressionCache.add(subExpressionDO);
                }
            }
            /*3、添加到列权限数据列表*/
            /*获取列权限数据*/
            List<DataRuleColVO> dataRuleColVOS = dataRuleSysVO.getCol();
            if(dataRuleColVOS!=null && !dataRuleColVOS.isEmpty()) {
                for (DataRuleColVO dataRuleColVO : dataRuleColVOS) {
                    DataRuleColDO dataRuleColDO = new DataRuleColDO();
                    BeanUtils.copyProperties(dataRuleColVO, dataRuleColDO);
                    dataRuleColDO.setDataRuleSysId(dataRuleSysId);
                    dataRuleColDO.setCreateTime(new Date());
                    dataRuleColDO.setCreateBy(operator);
                    dataRuleColCache.add(dataRuleColDO);
                }
            }
        }
    }

    /**
     * Description: 新增该方案对应的数据（包括对应的数据权限Sys、行权限、列权限）
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 16:43
     * @see
     */
    private void insertDataRuleTemlDatas(DataRuleTemplVO templVO, String operator) {
        /*1、新增数据权限模板 urc_data_rule_templ*/
        DataRuleTemplDO dataRuleTemplDO = new DataRuleTemplDO();
        BeanUtils.copyProperties(templVO, dataRuleTemplDO);
        Long templId = seqBp.getNextDataRuleTemplId();
        templVO.setTemplId(templId.toString());
        dataRuleTemplDO.setTemplId(templId);
        dataRuleTemplDO.setCreateBy(operator);
        dataRuleTemplDO.setCreateTime(new Date());
        dataRuleTemplDO.setModifiedTime(new Date());
        dataRuleTemplDO.setModifiedBy(operator);
        dataRuleTemplMapper.insert(dataRuleTemplDO);
        /*2、新增数据权限sys urc_data_rule_sys */
        /*数据权限Sys列表*/
        List<DataRuleSysDO> dataRuleSysCache = new ArrayList<>();
        /*列权限数据列表*/
        List<DataRuleColDO> dataRuleColCache = new ArrayList<>();
        /*行权限数据列表*/
        List<ExpressionDO> expressionCache = new ArrayList<>();

        List<DataRuleSysVO> dataRuleSysVOS = templVO.getLstDataRuleSys();
        if (dataRuleSysVOS != null) {
            assembleDataRuleSysDatas(dataRuleSysCache, dataRuleColCache, expressionCache, dataRuleSysVOS, templId, operator);
            /*批量新增数据权限Sys*/
            if(dataRuleSysCache!=null && !dataRuleSysCache.isEmpty()){
                dataRuleSysMapper.insertBatch(dataRuleSysCache);
            }
             /*批量新增行权限数据*/
            if(expressionCache!=null && !expressionCache.isEmpty()) {
                expressionMapper.insertBatch(expressionCache);
            }
            /*批量新增列权限数据*/
            if(dataRuleColCache!=null && !dataRuleColCache.isEmpty()) {
                dataRuleColMapper.insertBatch(dataRuleColCache);
            }
        }


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
            dataRuleTemplVO.setTemplId(String.valueOf(dataRuleTemplDO.getTemplId()));
            dataRuleTemplVO.setCreateTimeStr(dataRuleTemplDO.getCreateTime()!=null?DateUtil.formatDate(dataRuleTemplDO.getCreateTime(),"yyyy-MM-dd HH:mm:ss"):null);
            dataRuleTemplVO.setModifiedTimeStr(dataRuleTemplDO.getModifiedTime()!=null?DateUtil.formatDate(dataRuleTemplDO.getModifiedTime(),"yyyy-MM-dd HH:mm:ss"):null);
            dataRuleTemplVOS.add(dataRuleTemplVO);
        }
        return dataRuleTemplVOS;
    }

    @Override
    public ResultVO getMyDataRuleTempl(String pageNumber, String pageData, String operator) {
        DataRuleTemplDO templDO = new DataRuleTemplDO();
        if (!roleMapper.isSuperAdminAccount(operator)) {
            templDO.setCreateBy(operator);
        }

        Query query = new Query(templDO, pageNumber, pageData);
        List<DataRuleTemplDO> dataRuleTempList = dataRuleTemplMapper.getMyDataRuleTempl(query);
        List<DataRuleTemplVO> dataRuleTempListVO = convertDoToVO(dataRuleTempList);
        int dataRuleTempCount = dataRuleTemplMapper.getMyDataRuleTemplCount(query);

        PageResultVO pageResultVO = new PageResultVO(dataRuleTempListVO, dataRuleTempCount, pageData);
        return VoHelper.getSuccessResult(pageResultVO);
    }

    /**
     * Description: 删除一个或多个方案 包括对应的数据权限Sys  行权限  列权限
     *
     * @param :
     * @return:
     * @auther: lvcr
     * @date: 2018/6/15 16:53
     * @see
     */
    @Override
    public ResultVO deleteDataRuleTempl(String jsonStr) {
         /*1、将json字符串转为Json对象*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、获取参数*/
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        String lstTemplIdStr = jsonObject.getString("lstTemplId");
        if (StringUtil.isEmpty(lstTemplIdStr)) {
            throw new URCBizException("parameter lstTemplIdStr is null", ErrorCode.E_000002);
        }
        List<Long> lstTemplId = StringUtility.jsonToList(lstTemplIdStr, Long.class);

        /*3、判断当前用户是否为管理员，普通用户只能删除自己管理的方案*/
        Boolean isAdmin = roleMapper.isSuperAdminAccount(operator);
        if (isAdmin) {
             /*4.1、根据templId列表批量删除数据权限方案模板*/
            dataRuleTemplMapper.delTemplDatasByIds(lstTemplId);
        } else {
             /*4.2、根据templId列表和创建人批量删除数据权限方案模板*/
            dataRuleTemplMapper.delTemplDatasByIdsAndCreatBy(lstTemplId, operator);
        }
        return VoHelper.getSuccessResult();
    }

    /**
     * Description: 创建或更新多个用户的数据权限
     *
     * @param jsonStr@return:
     * @auther: lvcr
     * @date: 2018/6/20 16:10
     * @see
     */
    @Override
    public ResultVO addOrUpdateDataRule(String jsonStr) {
         /*1、将json字符串转为Json对象*/
        JSONObject jsonObject = StringUtility.parseString(jsonStr);
        /*2、获取参数*/
        String operator = jsonObject.getString("operator");
        if (StringUtil.isEmpty(operator)) {
            throw new URCBizException("parameter operator is null", ErrorCode.E_000002);
        }
        String lstDataRuleStr = jsonObject.getString("lstDataRule");
        if (StringUtil.isEmpty(lstDataRuleStr)) {
            throw new URCBizException("parameter lstDataRule is null", ErrorCode.E_000002);
        }
        List<JSONObject> sourceDataRuleVOS = StringUtility.parseObject(lstDataRuleStr, List.class);
        List<DataRuleVO> dataRuleVOS = convertList(sourceDataRuleVOS);
        if (dataRuleVOS == null || dataRuleVOS.isEmpty()) {
            throw new URCBizException("parameter lstDataRule is null", ErrorCode.E_000002);
        }
        List<String> lstUserName = new ArrayList<>();
        for (DataRuleVO dataRuleVO : dataRuleVOS) {
            lstUserName.add(dataRuleVO.getUserName());
        }
        /*1、删除用户列表对应的数据权限 包括数据权限Sys   行权限  列权限*/
        dataRuleMapper.delBatchByUserNames(lstUserName);
        List<DataRuleDO> dataRuleDOSCache = new ArrayList<>();
         /*数据权限Sys列表*/
        List<DataRuleSysDO> dataRuleSysCache = new ArrayList<>();
        /*列权限数据列表*/
        List<DataRuleColDO> dataRuleColCache = new ArrayList<>();
        /*行权限数据列表*/
        List<ExpressionDO> expressionCache = new ArrayList<>();
        /*2、新增用户-操作权限关系数据 dataRule*/
        for (DataRuleVO dataRuleVO : dataRuleVOS) {
            DataRuleDO dataRuleDO = new DataRuleDO();
            dataRuleDO.setCreateBy(operator);
            dataRuleDO.setCreateTime(new Date());
            Long dataRuleId = seqBp.getNextDataRuleId();
            dataRuleDO.setDataRuleId(dataRuleId);
            dataRuleDO.setUserName(dataRuleVO.getUserName());
            dataRuleDOSCache.add(dataRuleDO);
            /*新增urc_data_rule_sys*/
            List<DataRuleSysVO> dataRuleSysVOS = dataRuleVO.getLstDataRuleSys();
            if (dataRuleSysVOS != null) {
                assembleDataRuleSysDatas(dataRuleSysCache, dataRuleColCache, expressionCache, dataRuleSysVOS, dataRuleId, operator);
            }
        }
        /*批量新增用户-数据权限关系*/
        if(dataRuleDOSCache!=null && !dataRuleDOSCache.isEmpty()){
            dataRuleMapper.insertBatch(dataRuleDOSCache);
        }
         /*批量新增数据权限Sys*/
        if(dataRuleSysCache!=null && !dataRuleSysCache.isEmpty()) {
            dataRuleSysMapper.insertBatch(dataRuleSysCache);
        }
             /*批量新增行权限数据*/
        if(expressionCache!=null && !expressionCache.isEmpty()) {
            expressionMapper.insertBatch(expressionCache);
        }
            /*批量新增列权限数据*/
        if(dataRuleColCache!=null && !dataRuleColCache.isEmpty()) {
            dataRuleColMapper.insertBatch(dataRuleColCache);
        }
        /*发送MQ*/
        sendToMq(dataRuleSysCache, lstUserName);
        return VoHelper.getSuccessResult();
    }

    private List<DataRuleVO> convertList(List<JSONObject> sourceDataRuleVOS) {
        List<DataRuleVO> dataRuleVOS = new ArrayList<>();
        for (JSONObject jsonObject : sourceDataRuleVOS) {
            DataRuleVO dataRuleVO = new DataRuleVO();
            dataRuleVO = StringUtility.parseObject(jsonObject.toJSONString(), DataRuleVO.class);
            dataRuleVOS.add(dataRuleVO);
        }
        return dataRuleVOS;
    }

    @Override
    public ResultVO getDataRuleByUser(List<String> lstUserName) {
        List<DataRuleVO> dataRuel = new ArrayList<DataRuleVO>();
        if(lstUserName!=null&&lstUserName.size()>0){
	        for (int i = 0; i < lstUserName.size(); i++) {
	        	UserDO userDO=new UserDO();
	        	userDO.setUserName(lstUserName.get(i));
	            //通过用户名得到sys_key
	            List<DataRuleSysDO> syskeyList = dataRuleSysMapper.getDataRuleSysByUserName(userDO);
	            List<DataRuleSysVO> lstDataRuleSys = new ArrayList<DataRuleSysVO>();
	            if(syskeyList!=null &&syskeyList.size()>0){
	            	for (int j = 0; j < syskeyList.size(); j++) {
	            		//通过sysKey得到 行权限
	            		List<ExpressionDO> expressionList = expressionMapper.listExpressionDOsBySysKey(syskeyList.get(j).getDataRuleSysId());
	            		//通过sysKey得到列权限
	            		List<DataRuleColDO> dataRuleColList = dataRuleColMapper.listRuleColBySysKey(syskeyList.get(j).getDataRuleSysId());
	            		List<DataRuleColVO> dataRuleColVOList = new ArrayList<DataRuleColVO>();
	            		for (DataRuleColDO colDO : dataRuleColList) {
	            			DataRuleColVO dataRuleColVO = new DataRuleColVO();
	            			BeanUtils.copyProperties(colDO, dataRuleColVO);
	            			dataRuleColVOList.add(dataRuleColVO);
	            		}
	            		List<ExpressionVO> expressionVOList = new ArrayList<ExpressionVO>();
	            		for (ExpressionDO expressionDO : expressionList) {
	            			ExpressionVO expressionVO = new ExpressionVO();
	            			if (!StringUtility.isNullOrEmpty(expressionDO.getOperValues())) {
	            				String operValues = expressionDO.getOperValues();
	            				List<String> operValuesArr = StringUtility.jsonToList(operValues, String.class);
	            				expressionVO.setOperValuesArr(operValuesArr);
	            			}
	            			BeanUtils.copyProperties(expressionDO, expressionVO);
	            			expressionVOList.add(expressionVO);
	            		}
	            		DataRuleSysVO dataRuleSysVO = new DataRuleSysVO();
	            		ExpressionVO expressionVO = new ExpressionVO();
	            		expressionVO.setSubWhereClause(expressionVOList);
	            		expressionVO.setIsAnd(1);
	            		dataRuleSysVO.sysKey = syskeyList.get(j).getSysKey();
	            		dataRuleSysVO.sysName=syskeyList.get(j).getSysName();
	            		dataRuleSysVO.col = dataRuleColVOList;
	            		dataRuleSysVO.row = expressionVO;
	            		lstDataRuleSys.add(dataRuleSysVO);
	            	}
	            	DataRuleVO dataRuleVO = new DataRuleVO();
	            	dataRuleVO.userName = lstUserName.get(i);
	            	dataRuleVO.lstDataRuleSys = lstDataRuleSys;
	            	dataRuel.add(dataRuleVO);
	            }
	        }
        }
          return VoHelper.getSuccessResult(dataRuel);
      }


}
