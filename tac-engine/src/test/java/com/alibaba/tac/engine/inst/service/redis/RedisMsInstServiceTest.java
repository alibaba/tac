package com.alibaba.tac.engine.inst.service.redis;

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.test.TacEnginTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author jinshuan.li 01/03/2018 14:42
 */
public class RedisMsInstServiceTest extends TacEnginTest {


    @Resource
    private RedisMsInstService redisMsInstService;

    TacInst tacInst = new TacInst();

    @Before
    public void before() {

        tacInst.setJarVersion("xxxxx");
        tacInst.setMsCode("abc");
        tacInst.setName("test");
    }

    @Test
    public void getAllTacMsInsts() {

        List<TacInst> allTacMsInsts = redisMsInstService.getAllTacMsInsts();

        assertNotNull(allTacMsInsts);
    }

    @Test
    public void getTacMsInst() {

        TacInst tacMsInst = redisMsInstService.getTacMsInst(1L);
        assertNotNull(tacMsInst);
    }

    @Test
    public void createTacMsInst() {

        TacInst tacMsInst = redisMsInstService.createTacMsInst(tacInst);

        assertTrue(tacMsInst.getId() > 0);
    }

    @Test
    public void removeMsInst() {
        Boolean success = redisMsInstService.removeMsInst(1L);
        assertTrue(success);

        TacInst tacMsInst = redisMsInstService.getTacMsInst(1L);

        assertNull(tacMsInst);
    }
}