package com.alibaba.tac.sdk.tangram4tac.lib;

import com.alibaba.tac.sdk.tangram4tac.Cell;
import com.alibaba.tac.sdk.tangram4tac.Container;
import com.alibaba.tac.sdk.tangram4tac.Style;

import java.util.List;

/**
 * Created by longerian on 2017/11/5.
 */
public abstract class OneChildContainer<T extends Style> extends Container<T> {

    public void addChild(Cell child) {
        addChild(child, -1);
    }

    public void addChild(Cell child, int index) {
        if (items.isEmpty() && child != null) {
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
            if (items.isEmpty()) {
                if (children.size() > 0) {
                    Cell child = children.get(0);
                    if (index < 0) {
                        items.add(child);
                    } else if (index == 0) {
                        items.add(0, child);
                    } else if (index > 0 && index < items.size()) {
                        items.add(index, child);
                    }
                }
            }
        }
    }

}
