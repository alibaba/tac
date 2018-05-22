package com.alibaba.tac.engine.test;

import com.alibaba.tac.engine.code.CodeLoadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jinshuan.li 26/02/2018 11:36
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TacEnginTest {

    static {
        try {
            ClassLoader classLoader = CodeLoadService.changeClassLoader();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
