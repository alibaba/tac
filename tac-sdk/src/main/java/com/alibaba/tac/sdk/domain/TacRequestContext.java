package com.alibaba.tac.sdk.domain;

import java.util.HashMap;
import java.util.Map;

public class TacRequestContext implements Context {


    private String appName;

    private String msCode;

    private long instId;

    @Override
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setMsCode(String msCode) {
        this.msCode = msCode;
    }

    public void setInstId(long instId) {
        this.instId = instId;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    private Map<String, Object> paramMap = new HashMap<String, Object>();

    @Override
    public String getMsCode() {
        return msCode;
    }

    @Override
    public long getInstId() {
        return instId;
    }

    @Override
    public Object get(String key) {
        return paramMap.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return paramMap.containsKey(key);
    }

    @Override
    public Map<String, Object> getParams() {
        return paramMap;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void putAll(Map<String, Object> paramMap) {
        if (paramMap != null || !paramMap.isEmpty()) {
            this.paramMap.putAll(paramMap);
        }
    }
}
