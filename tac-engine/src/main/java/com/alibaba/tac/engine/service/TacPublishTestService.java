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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.inst.service.IMsInstFileService;
import com.alibaba.tac.engine.inst.service.IMsInstService;
import com.alibaba.tac.engine.inst.service.LocalMsInstFileService;
import com.alibaba.tac.sdk.common.TacContants;
import com.alibaba.tac.sdk.common.TacParams;
import com.alibaba.tac.sdk.common.TacResult;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.HttpConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * @author jinshuan.li 06/03/2018 14:34
 */
@Slf4j
@Service
public class TacPublishTestService {

    @Resource
    private IMsInstService msInstService;

    @Resource(name = "prePublishMsInstFileService")
    private IMsInstFileService prePublishMsInstFileService;

    @Resource
    private LocalMsInstFileService localMsInstFileService;

    @Resource
    private TacInstRunService tacInstRunService;

    @Resource
    private TacEngineService tacEngineService;

    @Value("${tac.container.web.api:http://localhost:8001/api/tac/execute}")
    private String containerWebApi;

    /**
     * prePublishTest
     *
     * @param instId
     * @param msCode
     * @param params
     * @return
     */
    public TacResult<Object> prePublishTest(Long instId, String msCode, Map<String, Object> params) throws Exception {

        TacInst tacMsInst = msInstService.getTacMsInst(instId);
        if (tacMsInst == null) {
            throw new IllegalArgumentException("the instance is not exist");
        }

        if (!StringUtils.equalsIgnoreCase(tacMsInst.getMsCode(), msCode)) {
            throw new IllegalArgumentException("the code is not match");
        }

        byte[] instanceFile = prePublishMsInstFileService.getInstanceFile(instId);

        if (instanceFile == null) {

            throw new IllegalStateException("can't get instance file");
        }
        // save data to local
        localMsInstFileService.saveInstanceFile(tacMsInst, instanceFile);

        // load and run
        return tacInstRunService.runWithLoad(msCode, instId, params);
    }

    /**
     * online publish test
     *
     * @param instId
     * @param msCode
     * @param params
     * @return
     */
    public TacResult<?> onlinePublishTest(Long instId, String msCode, Map<String, Object> params) {

        // 走http请求调用

        if (params == null) {
            params = Maps.newHashMap();
        }
        params.put(TacContants.DEBUG, true);

        return onlinePublishTestHttp(instId, msCode, params);

    }

    /**
     * test with http .
     *
     * @param instId
     * @param msCode
     * @param params
     * @return
     */
    private TacResult<?> onlinePublishTestHttp(Long instId, String msCode, Map<String, Object> params) {

        AsyncHttpClient asyncHttpClient = asyncHttpClient();

        ListenableFuture<Response> execute = asyncHttpClient.preparePost(containerWebApi + "/" + msCode)
            .addHeader("Content-Type", "application/json;charset=UTF-8").setBody(JSONObject.toJSONString(params))
            .execute();
        Response response;
        try {
            response = execute.get(10, TimeUnit.SECONDS);
            if (response.getStatusCode() == HttpConstants.ResponseStatusCodes.OK_200) {
                TacResult tacResult = JSONObject.parseObject(response.getResponseBody(), TacResult.class);
                return tacResult;
            }
            log.error("onlinePublishTestHttp msCode:{} params:{} {}", msCode, params, response);
            throw new IllegalStateException("request engine error " + msCode);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

    }

}
