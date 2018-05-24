package com.alibaba.tac.sdk.tangram4tac;

import com.alibaba.tac.sdk.tangram4tac.render.DefaultRender;
import com.alibaba.tac.sdk.tangram4tac.render.IRender;

/**
 * Created by longerian on 2017/11/5.
 */
public abstract class Cell<T extends Style> {

    protected String id;

    protected final String type;

    protected T style;

    protected String reuseId;

    @FieldExcluder
    protected IRender mRender;

    public Cell() {
        this.type = getType();
        this.mRender = new DefaultRender();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReuseId(String reuseId) {
        this.reuseId = reuseId;
    }

    public String getId() {
        return id;
    }

    public void setStyle(T style) {
        this.style = style;
    }

    public T getStyle() {
        return style;
    }

    public String getReuseId() {
        return reuseId;
    }

    public void setRender(IRender mRender) {
        this.mRender = mRender;
    }

    public abstract String getType();

    public Object render() {
        if (mRender != null) {
            return mRender.renderTo(this);
        }
        return null;
    }
}
