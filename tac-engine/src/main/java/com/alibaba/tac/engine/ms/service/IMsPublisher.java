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

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.ms.domain.TacMs;
import com.alibaba.tac.engine.ms.domain.TacMsDO;
import com.google.common.hash.Hashing;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;

/**
 * @author jinshuan.li 26/02/2018 10:04
 */
public interface IMsPublisher {

    /**
     * publish the instance
     *
     * @param tacInst
     * @return
     */
    Boolean publish(TacInst tacInst);

    /**
     * publish the instance with data
     *
     * @param tacInst
     * @param data
     * @return
     */
    Boolean publish(TacInst tacInst, byte[] data);

    /**
     * pre publish with data
     *
     * @param tacInst
     * @param data
     * @return
     */
    Boolean prePublish(TacInst tacInst, byte[] data);

    /**
     * git 预发
     * @param tacMsDO
     * @param tacInst
     * @return
     */
    TacInst gitPrePublish(TacMsDO tacMsDO,TacInst tacInst);
    /**
     * offline instance
     *
     * @param tacInst
     * @return
     */
    Boolean offline(TacInst tacInst);

    /**
     * check the instance data sign
     *
     * @param tacInst
     * @param instanceFile
     */
    default void checkSign(TacInst tacInst, byte[] instanceFile) {

        String md5 = Hashing.md5().hashBytes(instanceFile).toString();
        if (!StringUtils.equalsIgnoreCase(tacInst.getJarVersion(), md5)) {
            throw new IllegalStateException("instance jar version check error " + tacInst.getId());
        }
    }

}
