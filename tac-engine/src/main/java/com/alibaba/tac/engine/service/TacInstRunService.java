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
import com.alibaba.tac.sdk.common.TacContants;
import com.alibaba.tac.sdk.common.TacResult;
import com.alibaba.tac.sdk.common.TacThreadLocals;
import com.alibaba.tac.sdk.domain.TacRequestContext;
import com.alibaba.tac.sdk.handler.DisposableHandler;
import com.alibaba.tac.sdk.handler.InitializingHandler;
import com.alibaba.tac.sdk.handler.TacHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshuan.li 12/02/2018 14:11
 */
@Service
public class TacInstRunService {

    private Logger LOGGER = LoggerFactory.getLogger(TacInstRunService.class);

    @Resource
    private CodeLoadService codeLoadService;

    /**
     * @param instId
     * @return
     */
    public TacResult<Object> runWithLoad(String msCode, Long instId, Map<String, Object> params) throws Exception {

        Class<TacHandler> clazz = codeLoadService.loadHandlerClass(instId, TacHandler.class);

        if (clazz == null) {
            throw new RuntimeException("can't find target class");
        }
        TacHandler handlerInstance = null;
        // get an instance
        handlerInstance = clazz.newInstance();

        // init resource when the class implements InitializingHandler
        if (InitializingHandler.class.isAssignableFrom(handlerInstance.getClass())) {
            InitializingHandler initializingHandler = (InitializingHandler)handlerInstance;
            LOGGER.info("InitializingHandler instId:{} init : {}", instId, initializingHandler.getClass());
            initializingHandler.afterPropertiesSet();
        }

        try {
            TacRequestContext context = new TacRequestContext();

            context.setMsCode(msCode);
            context.setInstId(instId);
            context.putAll(params);

            // clear threadlocals before invoke
            TacThreadLocals.clear();

            Map<String, Object> tacParams = new HashMap<>(5);
            tacParams.put(TacContants.MS_CODE, msCode);
            tacParams.put(TacContants.DEBUG, true);

            TacThreadLocals.TAC_PARAMS.set(tacParams);

            // run
            TacResult execute = handlerInstance.execute(context);

            // get user log
            String runLog = getRunLog();

            execute.setMsgInfo(runLog);

            // dispose resource
            try {
                if (DisposableHandler.class.isAssignableFrom(handlerInstance.getClass())) {
                    DisposableHandler disposableHandler = (DisposableHandler)handlerInstance;
                    LOGGER.info("DisposableHandler distory . instId:{} {}", instId, disposableHandler.getClass());
                    disposableHandler.destroy();
                }
            } catch (Exception ex) {
                LOGGER.error("DisposableHandler error", ex);
            }
            return execute;
        } catch (Throwable e) {

            throw new RuntimeException(e);
        } finally {
            TacThreadLocals.clear();
        }

    }

    public String getRunLog() {

        StringBuilder stringBuilder = TacThreadLocals.TAC_LOG_CONTENT.get();
        if (stringBuilder != null) {
            return stringBuilder.toString();
        }
        return "";
    }

}
