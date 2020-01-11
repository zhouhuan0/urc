package com.yks.urc.fw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BeanProvider implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanProvider.class);

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> var1) throws BeansException {
        return ctx.getBeansOfType(var1);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return ctx.getBean(beanName, clazz);
    }

    public static <T> T getBean(Class<T> clazz) {
        return ctx.getBean(clazz);
    }

    /**
     * 找不到bean返回null
     *
     * @return
     * @Author panyun@youkeshu.com
     * @Date 2019/4/24 9:23
     */
    public static <T> T getBeanNoException(Class<T> clazz) {
        try {
            return ctx.getBean(clazz);
        } catch (Exception ex) {
            return null;
        }
    }
}
