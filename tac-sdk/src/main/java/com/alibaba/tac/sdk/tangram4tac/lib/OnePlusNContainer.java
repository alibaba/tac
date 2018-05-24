package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Container;

/**
 * Created by longerian on 2017/11/5.
 */
public class OnePlusNContainer extends Container<OnePlusNStyle> {

    public OnePlusNContainer() {
    }

    @Override
    public String getType() {
        return CellType.TYPE_CONTAINER_ON_PLUSN;
    }
}
