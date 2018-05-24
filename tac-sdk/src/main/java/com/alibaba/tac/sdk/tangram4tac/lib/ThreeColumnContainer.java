package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Container;

/**
 * Created by longerian on 2017/11/5.
 */
public class ThreeColumnContainer extends Container<FlowStyle> {

    public ThreeColumnContainer() {
    }

    @Override
    public String getType() {
        return CellType.TYPE_CONTAINER_3C_FLOW;
    }
}
