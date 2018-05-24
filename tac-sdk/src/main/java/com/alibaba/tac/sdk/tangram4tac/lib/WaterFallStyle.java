package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Style;

/**
 * Created by longerian on 2017/11/14.
 */
public class WaterFallStyle extends Style {

    protected float vGap;

    protected float hGap;

    protected float gap;

    protected int column;

    public float getvGap() {
        return vGap;
    }

    public void setvGap(float vGap) {
        this.vGap = vGap;
    }

    public float gethGap() {
        return hGap;
    }

    public void sethGap(float hGap) {
        this.hGap = hGap;
    }

    public float getGap() {
        return gap;
    }

    public void setGap(float gap) {
        this.gap = gap;
        this.vGap = gap;
        this.hGap = gap;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
