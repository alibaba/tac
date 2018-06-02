package com.tmall.itemcenter;

import lombok.Data;

/**
 * @author jinshuan.li 10/03/2018 15:43
 */
@Data
public class ItemDO {

    /**
     * itemID
     */
    private Long id;

    /**
     * item name
     */
    private String name;

    /**
     * item price
     */
    private String price;

    public ItemDO(Long id, String name, String price) {

        this.id = id;
        this.name = name;
        this.price = price;
    }
}
