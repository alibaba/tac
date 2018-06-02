package com.tmall.itemcenter;

import org.springframework.stereotype.Service;

/**
 * @author jinshuan.li 10/03/2018 15:43
 */
@Service
public class TmallItemService {

    /**
     * get a item
     *
     * @param id
     * @return
     */
    public ItemDO getItem(Long id) {

        // mock data
        return new ItemDO(id, "A Song of Ice and Fire", "￥222.00");
    }

}
