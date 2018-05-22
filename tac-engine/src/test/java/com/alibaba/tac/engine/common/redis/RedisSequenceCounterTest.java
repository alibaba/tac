package com.alibaba.tac.engine.common.redis;

import com.alibaba.tac.engine.test.TacEnginTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * @author jinshuan.li 01/03/2018 11:01
 */
public class RedisSequenceCounterTest extends TacEnginTest {


    @Resource
    RedisSequenceCounter redisSequenceCounter;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void set() {

        redisSequenceCounter.set(10);
        Long data = redisSequenceCounter.get();

        assertTrue(data.equals(10L));
    }

    @Test
    public void get() {


    }

    @Test
    public void incrementAndGet() {

        long data = redisSequenceCounter.incrementAndGet();


        Long data2 = redisSequenceCounter.get();
        //assertTrue(data==11);
    }

    @Test
    public void increBy() {
    }
}