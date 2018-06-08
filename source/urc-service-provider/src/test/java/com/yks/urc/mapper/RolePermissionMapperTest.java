package com.yks.urc.mapper;

import com.yks.urc.entity.RoleDO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉
 * 角色-功能权限mapper单元测试类
 *
 * @author lvcr
 * @version 1.0
 * @date 2018/6/7 16:12
 * @see RolePermissionMapperTest
 * @since JDK1.8
 */
public class RolePermissionMapperTest extends BaseMapperTest {

    @Autowired
    private IRolePermissionMapper rolePermissionMapper;

    @Test
    public void deleteBatch() {
        List<Integer> ids = new ArrayList<>();
        ids.add(1);
        ids.add(3);
        ids.add(4);
        int rtn = rolePermissionMapper.deleteBatch(ids);
        System.out.println(rtn);

    }


}
