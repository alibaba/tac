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

package com.alibaba.tac.engine.ms.domain;

import com.alibaba.tac.engine.inst.domain.TacInst;
import com.alibaba.tac.engine.properties.TacMsConstants;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jinshuan.li 27/02/2018 13:28
 *
 * the publish meta data
 */
@Data
public class TacMsPublishMeta implements Serializable {
    private static final long serialVersionUID = 7774540294768819287L;

    private TacInst tacInst;

    private Integer status = TacMsConstants.INST_STATUS_ONLINE;

    public TacMsPublishMeta() {
    }

    public TacMsPublishMeta(TacInst tacInst, Integer status) {
        this.tacInst = tacInst;
        this.status = status;
    }

    public TacMsPublishMeta(TacInst tacInst) {
        this.tacInst = tacInst;
    }
}
