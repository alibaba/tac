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
import com.alibaba.tac.engine.inst.service.DevMsInstFileService;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * @author jinshuan.li 01/03/2018 15:22
 */
public abstract class AbstractDefaultMsPublisher implements IMsPublisher {

    @Resource
    private DevMsInstFileService devMsInstFileService;

    @Override
    public Boolean publish(TacInst tacInst) {

        long instId = tacInst.getId();

        //1.1  get data
        byte[] instanceFile = devMsInstFileService.getInstanceFile(instId);

        if (instanceFile == null) {
            throw new IllegalArgumentException(
                MessageFormat.format("can't find local instance file . instId {0}", instId));
        }

        //1.2 check sign

        checkSign(tacInst, instanceFile);

        return this.publish(tacInst, instanceFile);
    }

    @Override
    public Boolean prePublish(TacInst tacInst, byte[] data) {
        return null;
    }

}
