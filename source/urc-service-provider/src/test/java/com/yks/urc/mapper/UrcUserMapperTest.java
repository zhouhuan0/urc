/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/8
 * @since 1.0.0
 */
package com.yks.urc.mapper;

import com.yks.urc.bp.impl.UserBp;
import com.yks.urc.entity.UrcUserDo;
import com.yks.urc.fw.StringUtility;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class UrcUserMapperTest extends BaseMapperTest {

    @Autowired
    private IUrcUserMapper urcUserMapper;

    @Autowired
    UserBp userBp;

    @Test
    public void insert() {
        insert2();
    }

    @Transactional
    public void insert2() {
        UrcUserDo userDo = new UrcUserDo();
        userDo.setUsername("test");
        userDo.setIsActive(1);
        userDo.setDingUserId("1232");
        userDo.setActiveTime(StringUtility.getDateTimeNow());
        userDo.setModifiedBy("lwx");
        userDo.setCreateBy("lwx");

        UrcUserDo userDo1 = new UrcUserDo();
        userDo1.setUsername("123");
        userDo1.setIsActive(1);
        userDo1.setDingUserId("1232");
        userDo1.setActiveTime(StringUtility.getDateTimeNow());
        userDo1.setModifiedBy("lll");
        userDo1.setCreateBy("lll");
        List<UrcUserDo> list = new ArrayList<>();
        list.add(userDo);
        list.add(userDo1);

        int delete = urcUserMapper.deleteUrcUser();
        System.out.println("-------------"+delete);
        int result = urcUserMapper.insertBatchUser(list);
        System.out.println(result);
//        userBp.insert();
    }
}
