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

package com.alibaba.tac.engine.ms.service.redis;

import com.alibaba.fastjson.JSON;
import com.alibaba.tac.engine.event.domain.GetAllMsEvent;
import com.alibaba.tac.engine.event.domain.MsOfflineEvent;
import com.alibaba.tac.engine.event.domain.MsReceivePublishEvent;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.ms.domain.TacMsPublishMeta;
import com.alibaba.tac.engine.ms.service.DefaultMsEventHandlers;
import com.alibaba.tac.engine.ms.service.IMsSubscriber;
import com.alibaba.tac.engine.properties.TacMsConstants;
import com.alibaba.tac.engine.properties.TacRedisConfigProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.alibaba.tac.engine.properties.TacMsConstants.INST_STATUS_ONLINE;

/**
 * @author jinshuan.li 2018/3/1 07:59
 */
@Slf4j
public class RedisMsSubscriber implements IMsSubscriber {

    @Resource
    private RedisMessageListenerContainer container;

    @Resource(name = "redisSubscribMessageAdapter")
    private MessageListenerAdapter messageListenerAdapter;

    @Resource
    private TacRedisConfigProperties tacRedisConfigProperties;

    @Resource(name = "redisTemplate")
    HashOperations<String, String, TacMsPublishMeta> hashOperations;

    @Resource
    private DefaultMsEventHandlers defaultMsEventHandlers;

    private Set<String> loadedMsCodes = Sets.newHashSet();

    /**
     * load all msCode
     */
    public void loadAllMsCode() {

        Map<String, TacMsPublishMeta> entries = hashOperations.entries(getMainKey());

        if (MapUtils.isEmpty(entries)) {
            return;
        }

        Collection<TacMsPublishMeta> values = entries.values();

        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        log.debug("on GetAllMsEvent : ", values);

        defaultMsEventHandlers.getPublisher().publishEvent(new GetAllMsEvent(Lists.newArrayList(entries.keySet())));

        entries.forEach((msCode, tacMsPublishMeta) -> {

            if (loadedMsCodes.contains(msCode)) {
                return;
            }

            handleOnePublish(tacMsPublishMeta, true);

            loadedMsCodes.add(msCode);
        });
    }

    private void handleOnePublish(TacMsPublishMeta publishMeta, Boolean isLoad) {

        log.debug("on tacInstData :{}", publishMeta);

        TacInst tacInst = publishMeta.getTacInst();
        if (publishMeta.getStatus().equals(INST_STATUS_ONLINE)) {
            log.debug("publish inst tacInst:{} isload:{}", tacInst, isLoad);
            defaultMsEventHandlers.getPublisher().publishEvent(new MsReceivePublishEvent(tacInst));
        } else if (publishMeta.getStatus().equals(TacMsConstants.INST_STATUS_OFFLINE)) {
            if (!isLoad) {
                log.debug("removePublished tacInst:{}", tacInst);
                defaultMsEventHandlers.getPublisher().publishEvent(new MsOfflineEvent(tacInst));
            }
        }

    }

    @Override
    public void subscribe() {

        container.addMessageListener(messageListenerAdapter,
            new ChannelTopic(tacRedisConfigProperties.getPublishEventChannel()));

        this.loadAllMsCode();

    }

    /**
     * receiveMessage, don't change the name and params
     *
     * @param message
     * @param channel
     */
    public void receiveMessage(String message, String channel) {

        if (!StringUtils.equalsIgnoreCase(channel, getPublishChannel())) {
            return;
        }

        log.debug("receiveMessage. channel:{} message:{}", channel, message);

        TacMsPublishMeta publishMeta = JSON.parseObject(message, TacMsPublishMeta.class);

        handleOnePublish(publishMeta, false);

    }

    private String getMainKey() {

        return tacRedisConfigProperties.getMsListPath();
    }

    private String getPublishChannel() {

        return tacRedisConfigProperties.getPublishEventChannel();
    }
}
