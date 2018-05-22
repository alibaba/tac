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

import com.alibaba.tac.engine.code.CodeLoadService;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.domain.TacInstanceInfo;
import com.alibaba.tac.engine.inst.service.IMsInstFileService;
import com.alibaba.tac.engine.inst.service.LocalMsInstFileService;
import com.alibaba.tac.sdk.handler.InitializingHandler;
import com.alibaba.tac.sdk.handler.TacHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author jinshuan.li 12/02/2018 17:27
 */
@Service
public class TacInstanceLoadService {

    private static Logger LOGGER = LoggerFactory.getLogger(TacInstanceLoadService.class);

    @Resource(name = "remoteMsInstFileService")
    private IMsInstFileService remoteMsInstFileService;

    @Resource
    private LocalMsInstFileService localMsInstFileService;

    @Resource
    private CodeLoadService codeLoadService;

    public TacInstanceInfo loadTacHandler(TacInst tacInst) throws Exception {

        LOGGER.info("TacInstanceLoader.loadTacHandler start,tacMs:{}", tacInst);
        if (tacInst == null) {
            return null;
        }
        //Step 1: download zip data
        TacInstanceInfo tacInstanceInfo = new TacInstanceInfo();

        long instId = tacInst.getId();
        byte[] bytes = remoteMsInstFileService.getInstanceFile(tacInst.getId());

        tacInstanceInfo.setJarVersion(tacInst.getJarVersion());
        tacInstanceInfo.setId(instId);

        LOGGER.info("TacInstanceLoader.loadTacHandler,msCode:{},instId:{},jarVersion:{}", tacInst.getMsCode(), instId,
            tacInst.getJarVersion());

        if (bytes == null || StringUtils.isEmpty(tacInstanceInfo.getJarVersion())) {
            throw new IllegalStateException("can't get jar file . instId:" + tacInst.getId());
        }

        localMsInstFileService.saveInstanceFile(tacInst, bytes);

        //Step 2: create CustomerClassLoader  load jar file

        Class<TacHandler> clazz = codeLoadService.loadHandlerClass(instId, TacHandler.class);
        if (clazz == null) {
            LOGGER.error("can't find  the calss {} from source. instId:{}", TacHandler.class.getCanonicalName(),
                instId);
            throw new IllegalStateException("can't find  the TacHandler.calss from source. instId:" + tacInst.getId());
        }
        TacHandler tacHandler = clazz.newInstance();

        LOGGER.info(
            "InitializingHandler init class : " + tacHandler.getClass() + " , isInit :" + InitializingHandler.class
                .isAssignableFrom(tacHandler.getClass()));

        //  init resource
        if (InitializingHandler.class.isAssignableFrom(tacHandler.getClass())) {
            InitializingHandler initializingHandler = (InitializingHandler)tacHandler;
            LOGGER.info("InitializingHandler init : " + initializingHandler.getClass());
            initializingHandler.afterPropertiesSet();
        }
        tacInstanceInfo.setTacHandler(tacHandler);

        LOGGER.info("TacInstanceLoader.loadTacHandler,instId {} end ..", instId);

        return tacInstanceInfo;
    }
}
