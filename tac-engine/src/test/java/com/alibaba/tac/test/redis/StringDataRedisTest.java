package com.alibaba.tac.test.redis;

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.util.Bytes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

/**
 * @author jinshuan.li 28/02/2018 19:44
 */
@SpringBootApplication(scanBasePackages = {"com.alibaba.tac.engine.redis"})
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StringDataRedisTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StringDataRedisTest {

    @Autowired
    private RedisTemplate<String, String> template;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Serializable> valueOperations;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, Long> valueOperations2;

    @Resource(name = "counterRedisTemplate")
    private RedisTemplate<String, String> counterRedisTemplate;


    private CountDownLatch countDownLatch = new CountDownLatch(1);

    @Test
    public void test() {

        TacInst tacInst = new TacInst();
        tacInst.setJarVersion("xxx");
        TacInst tacInst1 = saveTest("name", tacInst);

        System.out.println(tacInst1);
    }

    @Test
    public void sendMessage() {

        template.convertAndSend("topicA", "hello world");

        System.out.println("xxx");
    }

    @Test
    public void sendMessage2() throws InterruptedException {

        while (true) {
            template.convertAndSend("topicA", "hello world" + System.currentTimeMillis());
            Thread.sleep(1000L);
        }

    }

    @Test
    public void testSub1() throws InterruptedException {

        System.out.println("testSub1");
        countDownLatch.await();
    }

    @Test
    public void testSub2() throws InterruptedException {

        System.out.println("testSub2");
        countDownLatch.await();
    }

    public <T extends Serializable> T saveTest(String key, T value) {

        valueOperations.set(key, value);

        T data = (T)valueOperations.get(key);

        return data;

    }

    @Test
    public void testIncre() {

        RedisConnection connection = template.getConnectionFactory().getConnection();
        byte[] key = Bytes.toBytes("ljinshuan123");
        Long incr = connection.incr(key);

        byte[] bytes = connection.get(key);

        connection.set(key, bytes);

        connection.incr(key);
        bytes = connection.get(key);

    }

    @Test
    public void testIncre2() {

        byte[] bytes = Bytes.toBytes("2");

        long a = 52;

        byte b = (byte)a;

    }

    @Test
    public void testIncre3() {

        ValueOperations<String, String> valueOperations = counterRedisTemplate.opsForValue();
        String key = "ahaha";
        String s = valueOperations.get(key);
        valueOperations.set(key, "1");
        Long increment = valueOperations.increment(key, 2L);
        valueOperations.get(key);

        System.out.println(s);
    }

}


