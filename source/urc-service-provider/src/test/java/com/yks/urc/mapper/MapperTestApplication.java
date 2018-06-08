package com.yks.urc.mapper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

/**
 * 〈一句话功能简述〉 mapper类对应单元测试，只需扫描com.yks.urc.mapper包即可
 * 
 * @author lvcr
 * @version 1.0
 * @see MapperTestApplication
 * @since JDK1.8
 * @date 2018/6/6 18:04
 */
@SpringBootApplication
@MapperScan(basePackages = "com.yks.urc.mapper")
//@ComponentScan(basePackages = { "com.yks.urc.mapper" })
@ComponentScan(basePackages = { "com.yks.urc.mapper", "com.yks.urc.**.impl" })
@PropertySource("classpath:constant-${spring.profiles.active}.properties")
public class MapperTestApplication {
}
