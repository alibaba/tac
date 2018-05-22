package com.alibaba.tac.engine.ms.service.redis;

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.DevMsInstFileService;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.test.TacEnginTest;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * @author jinshuan.li 01/03/2018 16:22
 */
public class RedisMsPublisherTest extends TacEnginTest {

    @Resource
    RedisMsPublisher redisMsPublisher;

    @Resource
    IMsInstService iMsInstService;

    @Resource
    private DevMsInstFileService devMsInstFileService;

    CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void publish() throws InterruptedException {

        TacInst abc = iMsInstService.getTacMsInst(1L);
        byte[] abcs = devMsInstFileService.getInstanceFile("abc");
        Boolean publish = redisMsPublisher.publish(abc, abcs);

        assertTrue(publish);

        countDownLatch.await();
    }

    @Test
    public void offline() throws InterruptedException {
        TacInst abc = iMsInstService.getTacMsInst(1L);
        Boolean offline = redisMsPublisher.offline(abc);
        assertTrue(offline);

        countDownLatch.await();
    }
}