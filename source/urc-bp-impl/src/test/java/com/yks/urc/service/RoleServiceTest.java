/*
 * 文件名：RoleServiceTest.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：测试RoleService相关功能
 * 创建人：OuJie
 * 创建时间：2018年06月13日
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc.service;

import com.alibaba.fastjson.JSON;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yks.urc.AbstractSpringTest;
import com.yks.urc.entity.RoleDO;
import com.yks.urc.mapper.IRoleMapper;
import com.yks.urc.service.api.IRoleService;
import com.yks.urc.vo.ResultVO;
import com.yks.urc.vo.RoleVO;
import com.yks.urc.vo.helper.VoHelper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Optional;

/**
 * 测试RoleService相关功能
 * @author OuJie
 * @version 1.2
 * @date 2018年06月13日
 * @see RoleServiceTest
 * @since JDK1.8
 */

public class RoleServiceTest extends AbstractSpringTest {

    @Autowired
    private IRoleMapper roleMapper;
    @Autowired
    private IRoleService roleService;

    String operator = "user";

    @Test
    public void testCheckDuplicateRoleName(){
        String newRoleName = "admin";
        Integer roleId = null;
        ResultVO<Integer> resultVO = roleService.checkDuplicateRoleName(operator, newRoleName, roleId);
        //当前角色名重复---新增情况
        Assert.assertEquals(Optional.of(resultVO.data), Optional.of(1));
//        当前角色名重复---修改情况
        roleId = 10;
        resultVO = roleService.checkDuplicateRoleName(operator, newRoleName, roleId);
        Assert.assertEquals(Optional.of(resultVO.data), Optional.of(1));
    }
}
