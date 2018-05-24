package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Container;

/**
 * Created by longerian on 2017/11/5.
 */
public class FourColumnContainer extends Container<FlowStyle> {

    public FourColumnContainer() {
    }

    @Override
    public String getType() {
        return CellType.TYPE_CONTAINER_4C_FLOW;
    }
}
