/*
 *   MIT License
 *
 *   Copyright (c) 2016 Alibaba Group
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.alibaba.tac.engine.service;

import com.alibaba.tac.engine.common.SequenceCounter;
import com.alibaba.tac.engine.common.redis.RedisSequenceCounter;
import com.alibaba.tac.engine.inst.service.redis.RedisMsInstFileService;
import com.alibaba.tac.engine.inst.service.redis.RedisMsInstService;
import com.alibaba.tac.engine.ms.service.IMsSubscriber;
import com.alibaba.tac.engine.ms.service.redis.RedisMsPublisher;
import com.alibaba.tac.engine.ms.service.redis.RedisMsService;
import com.alibaba.tac.engine.ms.service.redis.RedisMsSubscriber;
import com.alibaba.tac.engine.util.ThreadPoolUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author jinshuan.li 01/03/2018 10:12
 *
 * the beans while use redis store
 */

@ConditionalOnProperty(name = "tac.default.store",havingValue = "redis")
@Configuration
public class RedisBeansConfig {

    @Bean
    public RedisTemplate redisTemplate(
        JedisConnectionFactory jedisConnectionFactory) {

        return getRedisTemplate(jedisConnectionFactory);
    }

    @Bean(name = "counterRedisTemplate")
    public RedisTemplate counterRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {

        return getCounterRedisTemplate(jedisConnectionFactory);
    }

    public static RedisTemplate getRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }

    /**
     * @param jedisConnectionFactory
     * @return
     */
    public static RedisTemplate getCounterRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        // the conter should use  StringRedisSerializer
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        // set thread pool
        container.setTaskExecutor(ThreadPoolUtils.createThreadPool(10, "tac-redis-subscribe-pool"));
        return container;
    }

    @Bean(name = "redisSubscribMessageAdapter")
    public MessageListenerAdapter listenerAdapter(IMsSubscriber messageListener) {

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageListener, "receiveMessage");

        return adapter;
    }

    /**
     * the redis implements beans
     */

    @Bean(name = "msInstIdCounter")
    public SequenceCounter msInstIdCounter() {
        return new RedisSequenceCounter("msInstIdCounter");
    }

    @Bean(name = "remoteMsInstFileService")
    public RedisMsInstFileService redisMsInstFileService() {
        return new RedisMsInstFileService(false);
    }


    @Bean(name = "prePublishMsInstFileService")
    public RedisMsInstFileService redisPrePublishMsInstFileService() {
        return new RedisMsInstFileService(true);
    }


    @Bean
    public RedisMsInstService redisMsInstService() {

        return new RedisMsInstService();
    }

    @Bean
    public RedisMsPublisher redisMsPublisher() {

        return new RedisMsPublisher();
    }

    @Bean
    public RedisMsService redisMsService() {
        return new RedisMsService();
    }

    @Bean
    public RedisMsSubscriber redisMsSubscriber() {

        return new RedisMsSubscriber();
    }

}
