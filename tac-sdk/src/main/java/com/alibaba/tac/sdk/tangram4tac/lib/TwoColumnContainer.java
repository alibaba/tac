package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Container;

/**
 * Created by longerian on 2017/11/5.
 */
public class TwoColumnContainer extends Container<FlowStyle> {

    public TwoColumnContainer() {
    }

    @Override
    public String getType() {
        return CellType.TYPE_CONTAINER_2C_FLOW;
    }
}
