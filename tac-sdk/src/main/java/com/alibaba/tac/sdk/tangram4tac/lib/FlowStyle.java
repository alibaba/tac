package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Style;

/**
 * Created by longerian on 2017/11/14.
 */
public class FlowStyle extends Style {

    protected int column;

    protected float vGap;

    protected float hGap;

    protected boolean autoExpand;

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public float getvGap() {
        return vGap;
    }

    public void setvGap(int vGap) {
        this.vGap = vGap;
    }

    public void setvGap(float vGap) {
        this.vGap = vGap;
    }

    public float gethGap() {
        return hGap;
    }

    public void sethGap(int hGap) {
        this.hGap = hGap;
    }

    public void sethGap(float hGap) {
        this.hGap = hGap;
    }

    public boolean isAutoExpand() {
        return autoExpand;
    }

    public void setAutoExpand(boolean autoExpand) {
        this.autoExpand = autoExpand;
    }
}
