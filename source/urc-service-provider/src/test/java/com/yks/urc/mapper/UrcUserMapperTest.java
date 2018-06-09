/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/8
 * @since 1.0.0
 */
package com.yks.urc.mapper;

import com.yks.urc.entity.UrcUserDo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UrcUserMapperTest extends BaseMapperTest {

    @Autowired
    private IUrcUserMapper urcUserMapper;

    @Test
    public void insert() {
        UrcUserDo userDo = new UrcUserDo();
        userDo.setUsername("test");
        userDo.setIsActive(1);
        userDo.setDingUserId("1232");
        userDo.setActiveTime(new Date());
        userDo.setModifiedBy("linwanxian");
        userDo.setCreateBy("linwanxian");
        List<UrcUserDo> list = new ArrayList<>();
        list.add(userDo);

        int delete = urcUserMapper.deleteUrcUser();
        System.out.println(delete);
        int result = urcUserMapper.insertBatchUser(list);
        System.out.println(result);
    }
}
