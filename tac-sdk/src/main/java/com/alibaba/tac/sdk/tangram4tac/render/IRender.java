package com.alibaba.tac.sdk.tangram4tac.render;

/**
 * Created by longerian on 2017/11/5.
 */
public interface IRender<I, O> {

    O renderTo(I input);

}
