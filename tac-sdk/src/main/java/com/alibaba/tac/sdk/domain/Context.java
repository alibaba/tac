package com.alibaba.tac.sdk.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * @author jinshuan.li 12/02/2018
 */
public interface Context extends Serializable {

    /**
     * get the msCode
     *
     * @return
     */
    String getMsCode();

    /**
     * get the instanceId
     *
     * @return
     */
    long getInstId();

    /**
     * get appName
     * @return
     */
    String getAppName();

    /**
     *  get the value in the params map
     */
    Object get(String key);

    /**
     *
     * @param key
     * @return
     */
    boolean containsKey(String key);

    /**
     *
     * @return
     */
    Map<String, Object> getParams();

}
