package com.alibaba.tac.test.http;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.tac.sdk.common.TacResult;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.asynchttpclient.util.HttpConstants;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.asynchttpclient.Dsl.asyncHttpClient;

/**
 * @author jinshuan.li 07/03/2018 15:41
 */
public class HttpClientTest {

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {

        JSONObject data = new JSONObject();
        data.put("name", "ljinshuan");

        AsyncHttpClient asyncHttpClient = asyncHttpClient();

        ListenableFuture<Response> execute = asyncHttpClient.preparePost("http://localhost:8001/api/tac/execute/shuan")
            .addHeader("Content-Type", "application/json;charset=UTF-8").setBody(data.toJSONString()).execute();
        Response response = execute.get(10, TimeUnit.SECONDS);

        if (response.getStatusCode() == HttpConstants.ResponseStatusCodes.OK_200) {
            TacResult tacResult = JSONObject.parseObject(response.getResponseBody(), TacResult.class);

            System.out.println(tacResult);
        }
        System.out.println(response);
    }

}
