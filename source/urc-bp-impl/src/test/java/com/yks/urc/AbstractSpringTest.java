/*
 * 文件名：AbstractSpringTest.java
 * 版权：Copyright by www.youkeshu.com
 * 描述：单元测试基类
 * 创建人：OuJie
 * 创建时间：2018年06月13日
 * 修改理由：
 * 修改内容：
 */
package com.yks.urc;

import com.yks.urc.service.api.IRoleService;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

/**
 * @author OuJie
 * @version 1.2
 * @date 2018年06月13日
 * @see AbstractSpringTest
 * @since JDK1.8
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-test.xml")
@Configuration
@Transactional
@Rollback
public class AbstractSpringTest implements ApplicationContextAware {

    private ApplicationContext context;

    private ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();


    public String getProperty(String key) {
        return context.getBean(Environment.class).getProperty(key);
    }

    public Resource[] getResources(String locationPattern) throws IOException {
        return resolver.getResources(locationPattern);
    }

    public DataSource getDataSource() {
        return context.getBean(DataSource.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
