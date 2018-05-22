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

package com.alibaba.tac.engine.ms.service;

import com.alibaba.tac.engine.event.domain.GetAllMsEvent;
import com.alibaba.tac.engine.event.domain.MsOfflineEvent;
import com.alibaba.tac.engine.event.domain.MsReceivePublishEvent;
import com.alibaba.tac.engine.service.TacInstanceContainerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author jinshuan.li 26/02/2018 20:13
 * <p>
 * 此处设置最高优先级
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefaultMsEventHandlers implements ApplicationEventPublisherAware {

    @Resource
    private TacInstanceContainerService tacInstanceContainerService;

    private ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void init() {

    }

    @Bean
    public ApplicationListener<GetAllMsEvent> getAllMsEventApplicationListener() {

        return new ApplicationListener<GetAllMsEvent>() {
            @Override
            public void onApplicationEvent(GetAllMsEvent event) {

            }
        };
    }

    @Bean
    public ApplicationListener<MsReceivePublishEvent> msReceivePublishEventApplicationListener() {

        return new ApplicationListener<MsReceivePublishEvent>() {
            @Override
            public void onApplicationEvent(MsReceivePublishEvent event) {
                try {
                    log.info("handle  MsReceivePublishEvent {}", event);

                    tacInstanceContainerService.loadTacInstance(event.getTacInst());
                } catch (Exception e) {
                    log.error("load instance error tacInst:{} {}", event.getTacInst(), e.getMessage(), e);
                    throw new IllegalStateException("load instance error " + e.getMessage());
                }
            }
        };
    }

    @Bean
    public ApplicationListener<MsOfflineEvent> msOfflineEventApplicationListener() {

        return new ApplicationListener<MsOfflineEvent>() {
            @Override
            public void onApplicationEvent(MsOfflineEvent event) {

                String msCode = event.getMsCode();

                tacInstanceContainerService.offlineMs(msCode);
            }
        };
    }

    public ApplicationEventPublisher getPublisher() {
        return applicationEventPublisher;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

        this.applicationEventPublisher = applicationEventPublisher;
    }
}
