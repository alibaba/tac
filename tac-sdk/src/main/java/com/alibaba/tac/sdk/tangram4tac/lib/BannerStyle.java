package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Style;

import java.util.Map;

/**
 * Created by longerian on 2017/11/15.
 */
public class BannerStyle extends Style {

    protected boolean autoScroll;

    protected Map<String, Object> specialInterval;

    protected boolean infinite;

    protected String indicatorImg1;

    protected String indicatorImg2;

    protected String indicatorGravity;

    protected String indicatorPosition;

    protected float indicatorGap;

    protected float indicatorHeight;

    protected float indicatorMargin;

    protected int infiniteMinCount;

    protected float pageRatio;

    protected float hGap;

    protected float scrollMarginLeft;

    protected float scrollMarginRight;

    protected float itemRatio;

    protected float indicatorRadius;

    protected String indicatorColor;

    protected String defaultIndicatorColor;

    public boolean isAutoScroll() {
        return autoScroll;
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public Map<String, Object> getSpecialInterval() {
        return specialInterval;
    }

    public void setSpecialInterval(Map<String, Object> specialInterval) {
        this.specialInterval = specialInterval;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public String getIndicatorImg1() {
        return indicatorImg1;
    }

    public void setIndicatorImg1(String indicatorImg1) {
        this.indicatorImg1 = indicatorImg1;
    }

    public String getIndicatorImg2() {
        return indicatorImg2;
    }

    public void setIndicatorImg2(String indicatorImg2) {
        this.indicatorImg2 = indicatorImg2;
    }

    public String getIndicatorGravity() {
        return indicatorGravity;
    }

    public void setIndicatorGravity(String indicatorGravity) {
        this.indicatorGravity = indicatorGravity;
    }

    public String getIndicatorPosition() {
        return indicatorPosition;
    }

    public void setIndicatorPosition(String indicatorPosition) {
        this.indicatorPosition = indicatorPosition;
    }

    public float getIndicatorGap() {
        return indicatorGap;
    }

    public void setIndicatorGap(float indicatorGap) {
        this.indicatorGap = indicatorGap;
    }

    public float getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setIndicatorHeight(float indicatorHeight) {
        this.indicatorHeight = indicatorHeight;
    }

    public float getIndicatorMargin() {
        return indicatorMargin;
    }

    public void setIndicatorMargin(float indicatorMargin) {
        this.indicatorMargin = indicatorMargin;
    }

    public int getInfiniteMinCount() {
        return infiniteMinCount;
    }

    public void setInfiniteMinCount(int infiniteMinCount) {
        this.infiniteMinCount = infiniteMinCount;
    }

    public float getPageRatio() {
        return pageRatio;
    }

    public void setPageRatio(float pageRatio) {
        this.pageRatio = pageRatio;
    }

    public float gethGap() {
        return hGap;
    }

    public void sethGap(float hGap) {
        this.hGap = hGap;
    }

    public float getScrollMarginLeft() {
        return scrollMarginLeft;
    }

    public void setScrollMarginLeft(float scrollMarginLeft) {
        this.scrollMarginLeft = scrollMarginLeft;
    }

    public float getScrollMarginRight() {
        return scrollMarginRight;
    }

    public void setScrollMarginRight(float scrollMarginRight) {
        this.scrollMarginRight = scrollMarginRight;
    }

    public float getItemRatio() {
        return itemRatio;
    }

    public void setItemRatio(float itemRatio) {
        this.itemRatio = itemRatio;
    }

    public float getIndicatorRadius() {
        return indicatorRadius;
    }

    public void setIndicatorRadius(float indicatorRadius) {
        this.indicatorRadius = indicatorRadius;
    }

    public String getIndicatorColor() {
        return indicatorColor;
    }

    public void setIndicatorColor(String indicatorColor) {
        this.indicatorColor = indicatorColor;
    }

    public String getDefaultIndicatorColor() {
        return defaultIndicatorColor;
    }

    public void setDefaultIndicatorColor(String defaultIndicatorColor) {
        this.defaultIndicatorColor = defaultIndicatorColor;
    }
}
