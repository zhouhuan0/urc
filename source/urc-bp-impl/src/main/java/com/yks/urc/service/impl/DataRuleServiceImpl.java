package com.yks.urc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yks.urc.entity.*;
import com.yks.urc.fw.StringUtility;
import com.yks.urc.mapper.*;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.vo.DataRuleSysVO;
import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
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
    private IUrcSqlMapper urcSqlMapper;

    @Autowired
    private IExpressionMapper expressionMapper;


    @Autowired
    private IDataRuleMapper dataRuleMapper;

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
    public ResultVO<DataRuleTemplVO> getDataRuleTemplByTemplId(String templId) {
        ResultVO resultVO = new ResultVO();
        DataRuleTemplVO dataRuleTemplVO = new DataRuleTemplVO();
        /**
         * 1、获取权限模板信息
         */
        Long tempId = Long.valueOf(templId);
        DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(tempId);
        BeanUtils.copyProperties(dataRuleTemplDO, dataRuleTemplVO);
        resultVO.data = dataRuleTemplVO;
        /**
         * 2、获取数据权限模板对应的数据权限sys
         */
        List<DataRuleSysDO> dataRuleSysDOList = dataRuleSysMapper.listByDataRuleId(tempId);
        if (dataRuleSysDOList == null || dataRuleSysDOList.isEmpty()) {
            logger.info("数据权限模板对应的数据权限sys为空");
            return resultVO;
        }
        /**
         * 3、获取urc_sql list数据
         */
        Set<Long> dataRuleSysIds = new HashSet<>();
        for (DataRuleSysDO dataRuleSysDO : dataRuleSysDOList) {
            dataRuleSysIds.add(dataRuleSysDO.getDataRuleSysId());
        }
        Long[] array = (Long[]) dataRuleSysIds.toArray();
        List<UrcSqlDO> urcSqlDOS = urcSqlMapper.listUrcSqlDOs(array);
        if (urcSqlDOS == null || urcSqlDOS.isEmpty()) {
            logger.info("数据权限模板对应的数据权限sys为空");
            return resultVO;
        }
        /**
         * 4、获取条件表达式列表信息
         */
        Set<Long> urcSqlIds = new HashSet<>();
        for (UrcSqlDO urcSqlDO : urcSqlDOS) {
            urcSqlIds.add(urcSqlDO.getSqlId());
        }
        Long[] urcSqlIdsArray = (Long[]) urcSqlIds.toArray();
        List<ExpressionDO> expressionDOS = expressionMapper.listExpressionDOs(urcSqlIdsArray);
        if (expressionDOS == null || expressionDOS.isEmpty()) {
            logger.info("数据权限模板对应的数据权限sys为空");
            return resultVO;
        }


        /**
         *
         */
        return null;
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
        String createBy = jsonObject.get("operator").toString();
        int currPage = Integer.valueOf(jsonObject.get("pageNumber").toString());
        int pageSize = Integer.valueOf(jsonObject.get("pageData").toString());
        DataRuleTemplVO dataRuleTemplVO = StringUtility.parseObject(jsonObject.get("templ").toString(), DataRuleTemplVO.class);
        String[] templNames = dataRuleTemplVO.templName.split("/r/n");
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("createBy", createBy);
        queryMap.put("templNames", templNames);
        queryMap.put("currIndex", (currPage - 1) * pageSize);
        queryMap.put("pageSize", pageSize);
        List<DataRuleTemplDO> dataRuleTemplDOS = dataRuleTemplMapper.listDataRuleTemplDOsByPage(queryMap);
        List<DataRuleTemplVO> dataRuleTemplVOS = convertDoToVO(dataRuleTemplDOS);
        Long total = dataRuleTemplMapper.getCounts();
        PageResultVO pageResultVO = new PageResultVO(dataRuleTemplVOS, total, pageSize);
        return VoHelper.getSuccessResult(pageResultVO);
    }

    /**
     * Description: 数据授权方案-快速分配
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
