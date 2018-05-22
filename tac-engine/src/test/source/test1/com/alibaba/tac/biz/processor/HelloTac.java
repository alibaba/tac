package com.alibaba.tac.biz.processor;

import com.alibaba.tac.sdk.common.TacResult;
import com.alibaba.tac.sdk.domain.Context;
import com.alibaba.tac.sdk.handler.TacHandler;

/**
 * @author jinshuan.li 12/02/2018 08:47
 */
public class HelloTac implements TacHandler<Integer> {
    @Override
    public TacResult<Integer> execute(Context context) throws Exception {

        System.out.println(context);
        System.out.println("hello tac");
        return TacResult.newResult(1);
    }
}
