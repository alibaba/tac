package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Style;

/**
 * Created by longerian on 2017/11/15.
 */
public class ScrollStyle extends Style {

    protected float pageWidth;

    protected float pageHeight;

    protected String defaultIndicatorColor;

    protected String indicatorColor;

    protected boolean hasIndicator;

    protected String footerType;

    protected boolean retainScrollState;

    public float getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(float pageWidth) {
        this.pageWidth = pageWidth;
    }

    public float getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(float pageHeight) {
        this.pageHeight = pageHeight;
    }

    public String getDefaultIndicatorColor() {
        return defaultIndicatorColor;
    }

    public void setDefaultIndicatorColor(String defaultIndicatorColor) {
        this.defaultIndicatorColor = defaultIndicatorColor;
    }

    public String getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(String indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public boolean isHasIndicator() {
        return hasIndicator;
    }

    public void setHasIndicator(boolean hasIndicator) {
        this.hasIndicator = hasIndicator;
    }

    public String getFooterType() {
        return footerType;
    }

    public void setFooterType(String footerType) {
        this.footerType = footerType;
    }

    public boolean isRetainScrollState() {
        return retainScrollState;
    }

    public void setRetainScrollState(boolean retainScrollState) {
        this.retainScrollState = retainScrollState;
    }
}
