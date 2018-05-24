package com.alibaba.tac.sdk.tangram4tac;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longerian on 2017/11/5.
 */
public abstract class Container<T extends Style> extends Cell<T> {

    protected Cell header;

    protected Cell footer;

    protected List<Cell> items;

    protected String load;
    /**
     * businessType 是提供给H5的兼容参数,对Native无意义
     */
    protected String businessType;

    protected int loadType;

    public Container() {
        super();
        items = new ArrayList<Cell>();
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Cell getHeader() {
        return header;
    }

    public void setHeader(Cell header) {
        this.header = header;
    }

    public Cell getFooter() {
        return footer;
    }

    public void setFooter(Cell footer) {
        this.footer = footer;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public void setLoadType(int loadType) {
        this.loadType = loadType;
    }

    public String getLoad() {
        return load;
    }

    public int getLoadType() {
        return loadType;
    }

    public void addChild(Cell child) {
        addChild(child, -1);
    }

    public void addChild(Cell child, int index) {
        if (child != null) {
            if (index < 0) {
                items.add(child);
            } else if (index == 0) {
                items.add(0, child);
            } else if (index > 0 && index < items.size()) {
                items.add(index, child);
            }
        }
    }

    public void addChildren(List<Cell> children) {
        addChildren(children, -1);
    }

    public void addChildren(List<Cell> children, int index) {
        if (children != null && !children.isEmpty()) {
            if (index < 0) {
                items.addAll(children);
            } else if (index == 0) {
                items.addAll(0, children);
            } else if (index > 0 && index < items.size()) {
                items.addAll(index, children);
            }
        }
    }

    public Cell getChildAt(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        } else {
            return null;
        }
    }

    public int getChildIndex(Cell child) {
        if (child != null) {
            return items.indexOf(child);
        } else {
            return -1;
        }
    }

    public void removeChild(Cell child) {
        if (child != null) {
            items.remove(child);
        }
    }

    public void removeAllChildren() {
        items.clear();
    }

    public void removeChildAt(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public List<Cell> getItems() {
        return items;
    }
}
