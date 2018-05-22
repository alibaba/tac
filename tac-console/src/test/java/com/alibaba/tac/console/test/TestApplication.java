package com.alibaba.tac.console.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author jinshuan.li 12/02/2018 09:26
 */
@SpringBootApplication(scanBasePackages = {"com.alibaba.tac"})
//@PropertySources({@PropertySource("classpath:test.properties")})
public class TestApplication {
}
