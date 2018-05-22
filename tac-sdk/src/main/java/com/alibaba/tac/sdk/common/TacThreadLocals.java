package com.alibaba.tac.sdk.common;

import java.util.Map;

public class TacThreadLocals {

    /**
     * the log data
     */
    public static final ThreadLocal<StringBuilder> TAC_LOG_CONTENT = new ThreadLocal<StringBuilder>();

    /**
     * the params data
     */
    public static final ThreadLocal<Map<String, Object>> TAC_PARAMS = new ThreadLocal<Map<String, Object>>();

    /**
     * clear data in threadlocal
     */
    public static void clear() {
        TAC_LOG_CONTENT.remove();
        TAC_PARAMS.remove();
    }

}
