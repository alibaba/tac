package com.alibaba.tac.test.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author jinshuan.li 28/02/2018 20:24
 */
@Slf4j
@Service
public class TacRedisMessageListener {

    /**
     * 接收消息
     *
     * @param message
     * @param channel
     */
    public void receiveMessage(String message, String channel) {

        log.info(message);
    }
}
