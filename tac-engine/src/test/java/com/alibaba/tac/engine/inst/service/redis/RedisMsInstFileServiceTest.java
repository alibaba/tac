package com.alibaba.tac.engine.inst.service.redis;

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.DevMsInstFileService;
import com.alibaba.tac.engine.test.TacEnginTest;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author jinshuan.li 01/03/2018 13:38
 */
public class RedisMsInstFileServiceTest extends TacEnginTest {

    @Resource
    private RedisMsInstFileService redisMsInstFileService;

    @Resource
    private DevMsInstFileService devMsInstFileService;

    private TacInst tacInst;

    @Before
    public void setUp() throws Exception {

        tacInst = new TacInst();
        tacInst.setJarVersion("abcccc");
        tacInst.setId(111);
        tacInst.setMsCode("abc");
        tacInst.setName("abc-test");
    }

    @Test
    public void getInstanceFile() {

        byte[] instanceFile = redisMsInstFileService.getInstanceFile(tacInst.getId());

        assertNotNull(instanceFile);
    }

    @Test
    public void saveInstanceFile() {

        Boolean aBoolean = redisMsInstFileService.saveInstanceFile(tacInst,
            devMsInstFileService.getInstanceFile(tacInst.getMsCode()));

        assertTrue(aBoolean);
    }
}