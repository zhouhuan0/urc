package com.yks.urc.service;

import com.yks.urc.service.api.IRoleService;
import org.junit.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

/**
 * 〈一句话功能简述〉 mapper类对应单元测试，只需扫描com.yks.urc.mapper包即可
 * 
 * @author lvcr
 * @version 1.0
 * @see ServiceTestApplication
 * @since JDK1.8
 * @date 2018/6/6 18:04
 */
@SpringBootApplication
//@MapperScan(basePackages = "com.yks.urc.mapper")
@ComponentScan(basePackages = { "com.yks.urc.mapper", "com.yks.urc.**.impl" })
@PropertySource("classpath:constant-${spring.profiles.active}.properties")
public class ServiceTestApplication {

    @Autowired
    private IRoleService roleService;

    @Test
    public void testDeleteRoles(){
        List<Integer> roleIds = new ArrayList<Integer>();
        roleIds.add(1);
        roleIds.add(2);
        roleIds.add(3);
        roleIds.add(4);
        roleService.deleteRoles(roleIds);
    }
}
