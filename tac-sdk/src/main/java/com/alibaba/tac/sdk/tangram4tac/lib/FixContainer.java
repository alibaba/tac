package com.alibaba.tac.sdk.tangram4tac.lib;

/**
 * Created by longerian on 2017/11/5.
 */
public class FixContainer extends OneChildContainer<FixStyle> {

    public FixContainer() {
        style = new FixStyle();
    }

    @Override
    public String getType() {
        return CellType.TYPE_CONTAINER_FIX;
    }

}
