package com.alibaba.tac.sdk.tangram4tac.lib;

/**
 * Created by longerian on 2017/11/5.
 */
public class StickyContainer extends OneChildContainer<StickyStyle> {

    public StickyContainer() {
    }

    @Override
    public String getType() {
        return CellType.TYPE_CONTAINER_STICKY;
    }
}
