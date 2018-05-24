package com.alibaba.tac.sdk.tangram4tac;

/**
 * Created by longerian on 2017/11/5.
 */
public class Style {

    @FieldNameMapper(key = "background-color")
    protected String backgroundColor;

    @FieldNameMapper(key = "background-image")
    protected String backgroundImage;

    protected float width;

    protected float height;

    protected int zIndex;

    protected String display;

    private float[] margin;

    private float[] padding;

    protected float[] cols;

    protected float aspectRatio;

    protected float ratio;

    protected boolean slidable;

    protected String forLabel;

    protected boolean disableReuse;

    public Style() {
    }

    public void setMargin(float top, float right, float bottom, float left) {
        if (this.margin == null) {
            this.margin = new float[4];
        }
        this.margin[0] = top;
        this.margin[1] = right;
        this.margin[2] = bottom;
        this.margin[3] = left;
    }

    public void setPadding(float top, float right, float bottom, float left) {
        if (this.padding == null) {
            this.padding = new float[4];
        }
        this.padding[0] = top;
        this.padding[1] = right;
        this.padding[2] = bottom;
        this.padding[3] = left;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public void setCols(float[] cols) {
        this.cols = cols;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getzIndex() {
        return zIndex;
    }

    public String getDisplay() {
        return display;
    }

    public float[] getMargin() {
        return margin;
    }

    public float[] getPadding() {
        return padding;
    }

    public float[] getCols() {
        return cols;
    }

    public float getAspectRatio() {
        return aspectRatio;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public boolean isSlidable() {
        return slidable;
    }

    public void setSlidable(boolean slidable) {
        this.slidable = slidable;
    }

    public String getForLabel() {
        return forLabel;
    }

    public void setForLabel(String forLabel) {
        this.forLabel = forLabel;
    }

    public boolean isDisableReuse() {
        return disableReuse;
    }

    public void setDisableReuse(boolean disableReuse) {
        this.disableReuse = disableReuse;
    }

}
