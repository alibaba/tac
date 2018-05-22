package com.alibaba.tac.engine.service;

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.domain.TacInstanceInfo;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.test.TestApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author jinshuan.li 12/02/2018 18:24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class TacInstanceLoadServiceTest {

    @Resource
    private TacInstanceLoadService tacInstanceLoadService;

    @Resource
    private IMsInstService iMsInstService;

    @Test
    public void loadTacHandler() throws Exception {
        TacInst tacMsInst = iMsInstService.getTacMsInst(111L);
        TacInstanceInfo tacInstanceInfo = tacInstanceLoadService.loadTacHandler(tacMsInst);

        assertNotNull(tacInstanceInfo);
    }
}