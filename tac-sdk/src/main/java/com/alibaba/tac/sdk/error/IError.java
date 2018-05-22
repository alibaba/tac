package com.alibaba.tac.sdk.error;

/**
 * @author jinshuan.li 2017/12/28 下午3:03
 */
public interface IError {

    /**
     * get error code
     * @return
     */
    int getCode();

    /**
     * get error message
     * @return
     */
    String getMessage();
}
