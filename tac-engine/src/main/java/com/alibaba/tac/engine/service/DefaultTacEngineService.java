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

import com.alibaba.tac.engine.inst.domain.TacInstanceInfo;
import com.alibaba.tac.engine.util.TacLogUtils;
import com.alibaba.tac.sdk.common.TacContants;
import com.alibaba.tac.sdk.common.TacParams;
import com.alibaba.tac.sdk.common.TacResult;
import com.alibaba.tac.sdk.common.TacThreadLocals;
import com.alibaba.tac.sdk.domain.TacRequestContext;
import com.alibaba.tac.sdk.handler.TacHandler;
import com.alibaba.tac.sdk.infrastracture.TacLogger;
import com.alibaba.tac.sdk.utils.TacIPUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jinshuan.li 12/02/2018 18:49
 */
@Service
public class DefaultTacEngineService implements TacEngineService, PriorityOrdered {

    private Logger LOGGER = LoggerFactory.getLogger(DefaultTacEngineService.class);
    @Resource
    private TacInstanceContainerService instanceContainerService;

    @Resource
    private TacInstRunService tacInstRunService;

    @Resource
    TacLogger TACLOG;

    @Override
    public TacResult<Map<String, Object>> execute(String msCode, TacParams params) {

        /** 参数校验 **/
        TacResult<Map<String, Object>> result = new TacResult<>(Collections.EMPTY_MAP);
        if (params == null || StringUtils.isEmpty(params.getAppName())) {
            TacLogUtils.warnRate(LOGGER, "{}^{}^{}^{}^{}", params.getAppName(), params.getMsCodes(), params.isBatch(),
                0, params.getParamMap());
            return TacResult.errorResult("PARAM_ERROR", "app name should not be empty...");
        }
        if (params == null || StringUtils.isEmpty(params.getMsCodes())) {
            TacLogUtils.warnRate(LOGGER, "{}^{}^{}^{}^{}", params.getAppName(), params.getMsCodes(), params.isBatch(),
                0, params.getParamMap());
            return TacResult.errorResult("PARAM_ERROR", "msCodes should not be empty...");
        }
        /** set context param **/
        TacRequestContext context = new TacRequestContext();
        this.setTacContext(context, params);
        // debug param
        String debug = String.valueOf(params.getParamValue(TacContants.DEBUG));
        Map<String, Object> resultDataMap = new HashMap<>();

        Map<String, Object> singleDateMap = new HashMap<>();
        long singleStartTime = System.currentTimeMillis();

        try {
            TacThreadLocals.clear();

            Map<String, Object> tacParams = new HashMap<>(5);
            if (params.getParamValue(TacContants.IP) != null) {
                tacParams.put(TacContants.IP, params.getParamValue(TacContants.IP));
            }
            tacParams.put(TacContants.MS_CODE, msCode);
            if (params.getParamValue(TacContants.DEBUG) != null) {
                tacParams.put(TacContants.DEBUG, String.valueOf(params.getParamValue(TacContants.DEBUG)));
            }

            TacThreadLocals.TAC_PARAMS.set(tacParams);

            TacInstanceInfo tacInstanceInfo = instanceContainerService.getInstanceFromCache(msCode);

            if (tacInstanceInfo == null) {
                this.setCommonFields(singleDateMap, msCode, "NOT_EXIST", msCode + ": the inst is not exist...", false, debug);
                resultDataMap.put(msCode, singleDateMap);
                result.setData(resultDataMap);
                return result;
            }

            context.setMsCode(msCode);
            context.setInstId(tacInstanceInfo.getId());

            /** execute user's ms code **/
            TacResult<?> singleResult = null;
            TacHandler tacHandler = tacInstanceInfo.getTacHandler();
            singleResult = tacHandler.execute(context);
            /** the result **/
            if (singleResult != null && singleResult.isSuccess()) {
                this.setCommonFields(singleDateMap, msCode, singleResult.getMsgCode(), singleResult.getMsgInfo(),
                    singleResult.isSuccess(), debug);
                if (singleResult.getHasMore() != null) {
                    singleDateMap.put("hasMore", singleResult.getHasMore());
                }
                singleDateMap.put("data", singleResult.getData());
                resultDataMap.put(msCode, singleDateMap);
            } else {
                this.setCommonFields(singleDateMap, msCode, singleResult.getMsgCode(), singleResult.getMsgInfo(),
                    singleResult.isSuccess(), debug);
                singleDateMap.put("data", singleResult.getData());
                resultDataMap.put(msCode, singleDateMap);
            }

        } catch (Exception e) {
            singleDateMap.put("msCode", msCode);
            singleDateMap.put("errorCode", "MicroService_EXCEPTION");
            TACLOG.error(
                "-------------------------------------------------------------------------------------YourCode "
                    + "Exception--------------------------------------------------------------------------------",
                e);
            singleDateMap.put("errorMsg", msCode + " error , please check your code");
            singleDateMap.put("ip", TacIPUtils.getLocalIp());
            singleDateMap.put("success", false);
            resultDataMap.put(msCode, singleDateMap);

        } finally {
            if ("true".equalsIgnoreCase(debug)) {
                result.setMsgInfo(TACLOG.getContent());
            }
            LOGGER.info("Single^{}^{}^{}^{}", params.getAppName(), context.getMsCode(), false,
                System.currentTimeMillis() - singleStartTime);
            TacThreadLocals.clear();
        }
        result.setData(resultDataMap);
        return result;

    }

    private void setCommonFields(Map<String, Object> singleDateMap, String msCode, String errorCode, String errorMsg,
                                 boolean success, String debug) {
        if (singleDateMap == null) {
            return;
        }
        singleDateMap.put("msCode", msCode);
        if (!success) {
            singleDateMap.put("errorCode", errorCode);
            singleDateMap.put("errorMsg", errorMsg);
        }
        if ("true".equalsIgnoreCase(debug)) {
            singleDateMap.put("ip", TacIPUtils.getLocalIp());
        }
        singleDateMap.put("success", success);

    }

    private void setTacContext(TacRequestContext context, TacParams params) {
        /** app name **/
        context.setAppName(params.getAppName());

        /** custom params **/
        context.putAll(params.getParamMap());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
