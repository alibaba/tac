package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Style;

/**
 * Created by longerian on 2017/11/14.
 */
public class FixStyle extends Style {

    protected float x;

    protected float y;

    protected String align;

    protected String showType;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }
}
