package com.alibaba.tac.test.redis;

import com.alibaba.tac.engine.common.redis.RedisSequenceCounter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import static com.alibaba.tac.engine.service.RedisBeansConfig.getCounterRedisTemplate;

/**
 * @author jinshuan.li 28/02/2018 19:52
 */
@Configuration("testRedisConfig")
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory jedisConnectionFactory,
                                                                       MessageListenerAdapter listenerAdapter) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        container.addMessageListener(listenerAdapter, new ChannelTopic("topicA"));
        // 设置线程池
        //container.setTaskExecutor(null);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(TacRedisMessageListener messageListener) {

        MessageListenerAdapter adapter = new MessageListenerAdapter(messageListener, "receiveMessage");

        return adapter;
    }

    @Bean(name = "counterRedisTemplate")
    public RedisTemplate counterRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {

        return  getCounterRedisTemplate(jedisConnectionFactory);
    }

    @Bean
    public RedisSequenceCounter redisSequenceCounter() {

        return new RedisSequenceCounter("redis.counter");
    }

}
