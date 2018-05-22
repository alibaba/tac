package com.alibaba.tac.sdk.handler;

import com.alibaba.tac.sdk.common.TacResult;
import com.alibaba.tac.sdk.domain.Context;

/**
 * the core interface in tac ,all the biz code should implements it
 *
 * @param <T>
 */
public interface TacHandler<T> {

    /**
     * execute the biz code
     * @param context
     * @return
     * @throws Exception
     */
    TacResult<T> execute(Context context) throws Exception;

}
