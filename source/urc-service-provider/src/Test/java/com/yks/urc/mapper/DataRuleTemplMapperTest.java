package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleTemplDO;
import com.yks.urc.entity.RoleDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

 /**
  * 〈一句话功能简述〉 
  *  功能权限模板Mapper操作类
  * @author lvcr
  * @version 1.0 
  * @see DataRuleTemplMapperTest 
  * @since JDK1.8
  * @date 2018/6/12 8:50
  */ 
public class DataRuleTemplMapperTest extends BaseMapperTest {

    @Autowired
    private IDataRuleTemplMapper dataRuleTemplMapper;

    @Test
    public void selectByTemplId() {
        DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(1L);
        Assert.assertNotNull(dataRuleTemplDO);
    }



}
