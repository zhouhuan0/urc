package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleSysDO;
import com.yks.urc.entity.DataRuleTemplDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 〈一句话功能简述〉
 *  数据权限sys Mapper操作类
 * @author lvcr
 * @version 1.0
 * @see DataRuleSysMapperTest
 * @since JDK1.8
 * @date 2018/6/12 8:50
 */
public class DataRuleSysMapperTest extends BaseMapperTest {

   @Autowired
   private IDataRuleSysMapper dataRuleSysMapper;

   @Test
   public void selectByTemplId() {
       List<DataRuleSysDO> dataRuleSysDOs = dataRuleSysMapper.listByDataRuleId(1L);
       Assert.assertNotNull(dataRuleSysDOs);
   }



}
