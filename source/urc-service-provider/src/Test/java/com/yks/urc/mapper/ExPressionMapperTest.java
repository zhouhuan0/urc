package com.yks.urc.mapper;

import com.yks.urc.entity.ExpressionDO;
import com.yks.urc.entity.UrcSqlDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 〈一句话功能简述〉
 * @author lvcr
 * @version 1.0
 * @see ExPressionMapperTest
 * @since JDK1.8
 * @date 2018/6/12 8:50
 */
public class ExPressionMapperTest extends BaseMapperTest {

   @Autowired
   private IExpressionMapper expressionMapper;

   @Test
   public void listExpressionDOs() {
       Long[] ids = {1L,2L,3L};
       List<ExpressionDO> expressionDOS = expressionMapper.listExpressionDOs(ids);
       Assert.assertNotNull(expressionDOS);
   }



}
