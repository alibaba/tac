package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Container;

/**
 * Created by longerian on 2017/11/5.
 */
public class ScrollFixBannerContainer extends Container<FixStyle> {

    @Override
    public String getType() {
        return CellType.TYPE_CONTAINER_SCROLL_FIX_BANNER;
    }
}
