/**
 * 〈一句话功能简述〉<br>
 * 〈〉
 *
 * @author linwanxian@youkeshu.com
 * @create 2018/6/14
 * @since 1.0.0
 */
package com.yks.urc.mapper;

import com.yks.urc.entity.PermissionDO;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public class PermissionMapperTest extends BaseMapperTest {

    @Autowired
    PermissionMapper permissionMapper;


    @Test
    public void getPermissionDoByRoleId(){
        Map<String,PermissionDO> map= permissionMapper.getSysContextByRoleId(1529743116993000004L);
        System.out.println(map);
    }



}
