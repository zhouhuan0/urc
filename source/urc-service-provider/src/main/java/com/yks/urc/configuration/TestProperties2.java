package com.yks.urc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@PropertySource("classpath:constant.properties")
public class TestProperties2 {

    @Value("${yks.location}")
    private String location;

    @PostConstruct
    public void test() {
        System.out.println(location);
    }
}
