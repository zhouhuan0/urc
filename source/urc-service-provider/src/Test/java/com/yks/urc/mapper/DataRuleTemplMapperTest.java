package com.yks.urc.mapper;

import com.yks.urc.entity.DataRuleTemplDO;
import com.yks.urc.entity.RoleDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 〈一句话功能简述〉
 * 功能权限模板Mapper操作类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/12 8:50
 * @see DataRuleTemplMapperTest
 * @since JDK1.8
 */
public class DataRuleTemplMapperTest extends BaseMapperTest {

    @Autowired
    private IDataRuleTemplMapper dataRuleTemplMapper;

    @Test
    public void selectByTemplId() {
        DataRuleTemplDO dataRuleTemplDO = dataRuleTemplMapper.selectByTemplId(1L);
        Assert.assertNotNull(dataRuleTemplDO);
    }

    @Test
    public void listDataRuleTemplDOsByPage() {
        //通过接收currPage参数表示显示第几页的数据，pageSize表示每页显示的数据条数。
        int currPage = 1;
        int pageSize = 3;

        Map<String, Object> map = new HashMap<>();
        String[] templNames = {"admin", "edison"};
        map.put("templNames", templNames);
        map.put("currIndex", (currPage - 1) * pageSize);
        map.put("pageSize", pageSize);

        List<DataRuleTemplDO> dataRuleTemplDOS = dataRuleTemplMapper.listDataRuleTemplDOsByPage(map);
        System.out.println(dataRuleTemplDOS);
    }


}
