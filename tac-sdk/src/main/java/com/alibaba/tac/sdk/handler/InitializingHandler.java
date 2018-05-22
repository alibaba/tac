package com.alibaba.tac.sdk.handler;

/**
 * TacHandler  init
 * Created by huchangkun on 2017/5/23.
 */
public interface InitializingHandler {

    void afterPropertiesSet() throws Exception;
}