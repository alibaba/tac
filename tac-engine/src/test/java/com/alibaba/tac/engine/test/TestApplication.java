package com.alibaba.tac.engine.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author jinshuan.li 12/02/2018 09:26
 */
@SpringBootApplication(
    scanBasePackages = {"com.alibaba.tac.engine", "com.alibaba.tac.infrastracture", "com.alibaba.tac.sdk","com.tmall.tac.test"})
@PropertySources({@PropertySource("classpath:test.properties")})
public class TestApplication {


}
