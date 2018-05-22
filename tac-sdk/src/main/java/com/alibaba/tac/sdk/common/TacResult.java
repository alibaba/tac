package com.alibaba.tac.sdk.common;

import lombok.Data;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author jinshuan.li 12/02/2018 08:10
 */
@Data
public class TacResult<T> implements Serializable {

    private static final long serialVersionUID = 2743787028659568164L;
    private boolean success = true;
    /**
     * the msgCode
     */
    private String msgCode;

    /**
     * the msgInfo
     */
    private String msgInfo;

    /**
     * the execute data
     */
    public T data;

    /**
     * hasMore data
     */
    public Boolean hasMore;

    private static String staticIp;

    /**
     * the tac engine host ip
     */
    private String ip;

    /**
     *
     *
     * @param data
     */
    public TacResult(T data) {
        this.data = data;
        this.ip = staticIp;
    }

    /**
     *
     *
     * @param msgCode
     * @param msgInfo
     * @param data
     */
    public TacResult(String msgCode, String msgInfo, T data) {
        this.msgCode = msgCode;
        this.msgInfo = msgInfo;
        this.data = data;
        this.ip = staticIp;
    }

    public static final <T> TacResult<T> newResult(T data) {
        TacResult<T> instance = new TacResult<T>(data);
        return instance;
    }

    public static final <T> TacResult<T> newResult(T data, boolean hasMore) {
        TacResult<T> instance = new TacResult<T>(data);
        instance.setHasMore(hasMore);
        return instance;
    }

    /**
     *
     *
     * @param msgCode
     * @param msgInfo
     * @return
     */
    public static final <T> TacResult<T> errorResult(String msgCode, String msgInfo) {
        TacResult<T> instance = new TacResult<T>(null);
        instance.success = false;
        instance.msgCode = msgCode;
        instance.msgInfo = msgInfo;
        return instance;
    }

    /**
     *
     *
     * @param msgCode
     * @return
     */
    public static final <T> TacResult<T> errorResult(String msgCode) {
        TacResult<T> instance = new TacResult<T>(null);
        instance.success = false;
        instance.msgCode = msgCode;
        instance.msgInfo = msgCode;
        return instance;
    }

    static {
        try {
            if (staticIp == null) {
                staticIp = Inet4Address.getLocalHost().getHostAddress();
            }
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

    }

}
