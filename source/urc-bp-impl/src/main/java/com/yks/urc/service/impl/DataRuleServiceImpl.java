package com.yks.urc.service.impl;

import com.yks.urc.entity.DataRuleSysDO;
import com.yks.urc.entity.DataRuleTemplDO;
import com.yks.urc.entity.ExpressionDO;
import com.yks.urc.entity.UrcSqlDO;
import com.yks.urc.mapper.IDataRuleSysMapper;
import com.yks.urc.mapper.IDataRuleTemplMapper;
import com.yks.urc.mapper.IExpressionMapper;
import com.yks.urc.mapper.IUrcSqlMapper;
import com.yks.urc.service.api.IDataRuleService;
import com.yks.urc.vo.DataRuleTemplVO;
import com.yks.urc.vo.PageResultVO;
import com.yks.urc.vo.ResultVO;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        for(DataRuleSysDO dataRuleSysDO:dataRuleSysDOList){
            dataRuleSysIds.add(dataRuleSysDO.getDataRuleSysId());
        }
        Long[] array = (Long[])dataRuleSysIds.toArray();
        List<UrcSqlDO> urcSqlDOS = urcSqlMapper.listUrcSqlDOs(array);
        if(urcSqlDOS==null || urcSqlDOS.isEmpty()){
            logger.info("数据权限模板对应的数据权限sys为空");
            return resultVO;
        }
        /**
         * 4、获取条件表达式列表信息
         */
        Set<Long> urcSqlIds = new HashSet<>();
        for(UrcSqlDO urcSqlDO:urcSqlDOS){
            urcSqlIds.add(urcSqlDO.getSqlId());
        }
        Long[] urcSqlIdsArray = (Long[])urcSqlIds.toArray();
        List<ExpressionDO> expressionDOS = expressionMapper.listExpressionDOs(urcSqlIdsArray);
        if(expressionDOS==null || expressionDOS.isEmpty()){
            logger.info("数据权限模板对应的数据权限sys为空");
            return resultVO;
        }



        /**
         *
         */
        return null;
    }

    @Override
    public ResultVO<PageResultVO> getDataRuleTempl(String jsonStr) {
        return null;
    }
}
