package com.alibaba.tac.sdk.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class TacParams implements Serializable {

    private static final long serialVersionUID = 6681979519642226666L;
    private String appName;

    /**
     * the service codes ,  required,  split with ','
     *
     */
    private String msCodes;

    /**
     * is batch execute
     *
     */
    private boolean isBatch = false;

    /**
     * the service params,  {@link TacContants}
     *
     */
    private Map<String, Object> paramMap = new HashMap<String, Object>();

    public TacParams(String appName, String msCodes) {
        this.appName = appName;
        this.msCodes = msCodes;
    }

    public String getParamValue(String paramKey) {
        if (StringUtils.isEmpty(paramKey)) {
            return StringUtils.EMPTY;
        }
        if (paramMap.get(paramKey) != null) {
            return String.valueOf(paramMap.get(paramKey));
        } else {
            return StringUtils.EMPTY;
        }
    }

    public void addPara(String key, Object value) {
        this.paramMap.put(key, value);
    }

}
