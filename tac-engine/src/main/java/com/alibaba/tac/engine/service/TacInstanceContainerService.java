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

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.domain.TacInstanceInfo;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.ms.service.IMsSubscriber;
import com.alibaba.tac.sdk.handler.DisposableHandler;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jinshuan.li 12/02/2018 17:27
 */
@Service
public class TacInstanceContainerService implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(TacInstanceLoadService.class);
    /**
     * cache
     */
    private Cache<String, TacInstanceInfo> tacInstCache = CacheBuilder.newBuilder().build();

    @Resource
    private IMsInstService iMsInstService;

    @Resource
    private TacInstanceLoadService tacInstanceLoadService;

    @Resource
    private IMsSubscriber msSubscriber;

    private AtomicBoolean initFlag = new AtomicBoolean(false);

    /**
     * the locks prevent repeat load
     */
    private Map<String, ReentrantLock> instanceLoadLocks = Maps.newConcurrentMap();

    public void init() {

        if (initFlag.compareAndSet(false, true)) {

            msSubscriber.subscribe();

        }

    }

    /**
     * get from cache
     *
     * @param msCode
     * @return
     */
    public TacInstanceInfo getInstanceFromCache(String msCode) {
        return tacInstCache.getIfPresent(msCode);
    }

    /**
     * load instance
     *
     * @param tacInst
     */
    public TacInstanceInfo loadTacInstance(TacInst tacInst) throws Exception {

        String msCode = tacInst.getMsCode();

        ReentrantLock reentrantLock = instanceLoadLocks.get(msCode);
        if (reentrantLock == null) {
            reentrantLock = new ReentrantLock();
            instanceLoadLocks.put(msCode, reentrantLock);
        }
        reentrantLock.lock();
        try {
            TacInstanceInfo existInstance = tacInstCache.getIfPresent(tacInst.getMsCode());

            if (existInstance == null) {

                TacInstanceInfo tacInstanceInfo = tacInstanceLoadService.loadTacHandler(tacInst);

                assert tacInstanceInfo != null;
                LOGGER.info("TacInstanceContainer loadTacInstance , result : {}", tacInstanceInfo);
                tacInstCache.put(msCode, tacInstanceInfo);

                return tacInstanceInfo;
            }

            if (StringUtils.equals(tacInst.getJarVersion(), existInstance.getJarVersion())) {
                LOGGER.debug("the exist instance has the same jarVersion ,skip. {}", tacInst.getJarVersion());
                LOGGER.info("TacInstanceContainer loadTacInstance has been load!");
                return existInstance;
            }

            TacInstanceInfo tacInstanceInfo = tacInstanceLoadService.loadTacHandler(tacInst);

            assert tacInstanceInfo != null;

            LOGGER.info("TacInstanceContainer loadTacInstance , result : {}", tacInstanceInfo);
            tacInstCache.put(msCode, tacInstanceInfo);
            LOGGER.info("TacInstanceContainer loadTacInstance,msCode:{},instId:{},oldVersilon:{},newVersion:{}", msCode,
                tacInst.getId(), existInstance.getJarVersion(), tacInstanceInfo.getJarVersion());
            if (existInstance != null) {
                this.disposeInstance(existInstance);
            }
            return tacInstanceInfo;
        } finally {
            reentrantLock.unlock();
        }

    }

    /**
     * destroy instance
     *
     * @param oldTacInstanceInfo
     */
    private void disposeInstance(TacInstanceInfo oldTacInstanceInfo) {

        try {
            if (oldTacInstanceInfo != null && DisposableHandler.class.isAssignableFrom(
                oldTacInstanceInfo.getTacHandler().getClass())) {
                DisposableHandler disposableHandler = (DisposableHandler)oldTacInstanceInfo.getTacHandler();
                LOGGER.info("TacInstanceContainer oldTacInstanceInfo distory : " + oldTacInstanceInfo.getTacHandler()
                    .getClass());
                disposableHandler.destroy();
            }
        } catch (Exception ex) {

            LOGGER.error("TacInstanceContainer DisposableHandler error", ex);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        try {
            this.init();
        } catch (Throwable e) {

            LOGGER.error(e.getMessage(), e);

            // throw exception immediatly when has error while start up
            throw new IllegalStateException("init tacInstanceInfo error");
        }
    }

    /**
     * offline Ms
     *
     * @param msCode
     */
    public void offlineMs(String msCode) {

        TacInstanceInfo tacInstanceInfo = tacInstCache.getIfPresent(msCode);
        if (tacInstanceInfo != null) {
            LOGGER.info("offlineMs msCode:{}", msCode);
            tacInstCache.invalidate(msCode);
            this.disposeInstance(tacInstanceInfo);
        }
    }
}
