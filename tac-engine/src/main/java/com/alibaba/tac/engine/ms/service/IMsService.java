/*
 *   MIT License
 *
 *   Copyright (c) 2016 Alibaba Group
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 */

package com.alibaba.tac.engine.ms.service;

import com.alibaba.tac.engine.ms.domain.TacMsDO;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author jinshuan.li 12/02/2018 17:18
 */
public interface IMsService {

    /**
     *  create ms
     *
     * @param tacMsDO
     * @return
     */
    TacMsDO createMs(TacMsDO tacMsDO);

    /**
     *  remove ms
     *
     * @param msCode
     * @return
     */
    Boolean removeMs(String msCode);

    /**
     *  invalid ms
     *
     * @param msCode
     * @return
     */
    Boolean invalidMs(String msCode);

    /**
     *  update ms
     *
     * @param msCode
     * @param tacMsDO
     * @return
     */
    Boolean updateMs(String msCode, TacMsDO tacMsDO);

    /**
     *  get ms
     *
     * @param msCode
     * @return
     */
    TacMsDO getMs(String msCode);

    /**
     *  get all ms
     *
     * @return
     */
    List<TacMsDO> getAllMs();

    default void checkMsDO(TacMsDO tacMsDO) {

        if (tacMsDO == null) {
            throw new IllegalArgumentException("tacMsDO is null");
        }

        if (StringUtils.isEmpty(tacMsDO.getCode()) || StringUtils.isEmpty(tacMsDO.getName())) {
            throw new IllegalArgumentException("code or name is empty");
        }



    }
}
